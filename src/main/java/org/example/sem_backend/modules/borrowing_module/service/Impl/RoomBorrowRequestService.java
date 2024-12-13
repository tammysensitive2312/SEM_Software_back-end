package org.example.sem_backend.modules.borrowing_module.service.Impl;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.RoomBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
import org.example.sem_backend.modules.borrowing_module.service.InterfaceRequestService;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.entity.RoomSchedule;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.example.sem_backend.modules.room_module.repository.RoomScheduleRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomBorrowRequestService implements InterfaceRequestService<RoomBorrowRequest, RoomBorrowRequestDTO> {
    private final RoomBorrowRequestRepository roomBorrowRequestRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomBorrowRequestMapper mapper;
    private final RoomScheduleRepository scheduleRepository;
    private final TransactionsLogRepository logRepository;

    /**
     * This method handles the entire process of booking a room, including:
     * - Validating the request
     * - Retrieving the room and user information
     * - Creating and saving a new room borrow request
     * - Creating and saving a new room schedule
     *
     * @param requestDto @{@link RoomBorrowRequestDTO}
     * @throws ResourceNotFoundException if the requested room or user is not found
     * @throws ResourceConflictException if the request is invalid (e.g., scheduling conflict)
     */
    @Override
    @Transactional
    @Async
    public void processRequest(RoomBorrowRequestDTO requestDto) {
        log.info("Processing room borrow request for Room ID: {}", requestDto.getRoomId());

        // Kiểm tra tính hợp lệ của yêu cầu mượn phòng
        validateRequest(requestDto);

        // Tìm Room từ roomId
        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Room not found with ID: [%s]", requestDto.getRoomId()),
                        "BORROWING-MODULE"
                ));

        // Tìm User từ userId
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User not found with ID: [%s]", requestDto.getUserId()),
                        "BORROWING-MODULE"
                ));

        // Tạo mới RoomBorrowRequest từ DTO
        RoomBorrowRequest request = mapper.toEntity(requestDto);
        request.setUser(user);
        request.setRoom(room);
        request.setComment(requestDto.getComment());

        // Lưu yêu cầu mượn phòng vào cơ sở dữ liệu
        roomBorrowRequestRepository.save(request);

        // Tạo lịch đặt phòng mới
        try {
            RoomSchedule schedule = new RoomSchedule();
            schedule.setRoom(room);
            schedule.setUser(user.getEmail());
            schedule.setStartTime(requestDto.getStartTime());
            schedule.setEndTime(requestDto.getEndTime());

            scheduleRepository.save(schedule);
        } catch (OptimisticLockException e) {
            throw new ResourceConflictException(
                    String.format("Rejected booking - Room ID [%s] has already been booked for [%s] to [%s]",
                            requestDto.getRoomId(),
                            requestDto.getStartTime(),
                            requestDto.getEndTime()),
                    "BORROWING-MODULE"
            );
        }

        TransactionsLog transactionsLog = new TransactionsLog();
        transactionsLog.setRoomRequest(request);
        transactionsLog.setTransactionType("borrow room");
        transactionsLog.setUser(user);

        logRepository.save(transactionsLog);

        log.info("Successfully processed room booking - Room ID: {}, User: {}, Time: {} to {}",
                requestDto.getRoomId(),
                user.getEmail(),
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );
    }



    /**
     * Validates a room borrowing request.
     * This method checks if the requested booking time is within the allowed future window
     * (not more than 2 weeks ahead) and if there are any scheduling conflicts with existing bookings.
     *
     * @param requestDto @{@link RoomBorrowRequestDTO}
     * @throws ResourceConflictException if the booking start time is more than 2 weeks in the future
     *         or if there are conflicting bookings for the requested time slot.
     */
    @Override
    public boolean validateRequest(RoomBorrowRequestDTO requestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxAllowedStartTime = now.plusWeeks(2);

        // Validate future booking window
        if (requestDto.getStartTime().isAfter(maxAllowedStartTime)) {
            throw new ResourceConflictException(
                    String.format("Rejected booking - Start time [%s] for Room ID [%s] is more than 2 weeks ahead",
                            requestDto.getStartTime(), requestDto.getRoomId()),
                    "BORROWING-MODULE"
            );
        }

        // Check for scheduling conflicts
        List<RoomSchedule> conflictingSchedules = scheduleRepository
                .findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(
                        requestDto.getRoomId(),
                        requestDto.getStartTime(),
                        requestDto.getEndTime()
                );

        if (!conflictingSchedules.isEmpty()) {
            throw new ResourceConflictException(
                    String.format("Rejected booking - Room ID [%s] has conflicting bookings during [%s] to [%s]",
                            requestDto.getRoomId(), requestDto.getStartTime(), requestDto.getEndTime()),
                    "BORROWING-MODULE"
            );
        }
        return true;
    }

    @Override
    @Transactional
    public void updateRequest(RoomBorrowRequestDTO requestDto) {
        validateRequest(requestDto);

        RoomBorrowRequest existingRequest = roomBorrowRequestRepository
                .findById(requestDto.getUniqueId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Request not found with ID: [%s]", requestDto.getUniqueId()),
                        "BORROWING-MODULE"));

        LocalDateTime now = LocalDateTime.now();

        // Check if the request was created more than 24 hours ago
        if (now.isAfter(existingRequest.getCreateAt().plusDays(1))) {
            throw new ResourceConflictException(
                    "Overdue correction time allowed. You can only update within 24 hours of creation.",
                    "BORROWING-MODULE"
            );
        }

        mapper.partialUpdate(requestDto, existingRequest);
        roomBorrowRequestRepository.save(existingRequest);
    }

    /**
     * no use
     */
    @Override
    public void approveRequest(Long requestId) {

    }

    /**
     * careful to use this feature
     * under development
     */
    @Override
    @Transactional
    public void deleteRequestsByIds(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            throw new IllegalArgumentException("Request IDs cannot be null or empty");
        }

        List<RoomBorrowRequest> requestsToDelete = roomBorrowRequestRepository.findAllById(requestIds);
        if (requestsToDelete.isEmpty()) {
            throw new ResourceNotFoundException("No Room Borrow Requests found for the given IDs", "BORROWING_MODULE");
        }

        logRepository.deleteByRoomRequestIds(requestIds);
        roomBorrowRequestRepository.deleteAllInBatch(requestsToDelete);
    }

    /**
     * Retrieves a paginated list of room borrow requests for a specific user.
     * Filters the requests based on the provided start and end time.
     *
     * @return A paginated list of room borrow requests for the user.
     */
    public Page<GetRoomRequestDTO> getUserRequests(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return roomBorrowRequestRepository.
                findRequestsWithSchedules(userId, null, startTime, endTime, pageable);
    }

    /**
     * Retrieves a paginated list of room borrow requests for administrative purposes.
     * Filters the requests based on username and the provided start and end time.
     *
     * @return A paginated list of room borrow requests for administrative use.
     */
    public Page<GetRoomRequestDTO> getAdminRequests(String email, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return roomBorrowRequestRepository.
                findRequestsWithSchedules(null, email, startTime, endTime, pageable);
    }
}