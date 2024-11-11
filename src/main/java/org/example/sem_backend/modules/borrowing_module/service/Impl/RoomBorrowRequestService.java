package org.example.sem_backend.modules.borrowing_module.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.RoomBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.service.InterfaceRequestService;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.entity.RoomSchedule;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.example.sem_backend.modules.room_module.repository.RoomScheduleRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.stereotype.Service;

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

        // Lưu yêu cầu mượn phòng vào cơ sở dữ liệu
        roomBorrowRequestRepository.save(request);

        // Tạo lịch đặt phòng mới
        RoomSchedule schedule = new RoomSchedule();
        schedule.setRoom(room);
        schedule.setUser(user.getUsername());
        schedule.setStartTime(requestDto.getStartTime());
        schedule.setEndTime(requestDto.getEndTime());

        // Lưu lịch đặt phòng vào cơ sở dữ liệu
        scheduleRepository.save(schedule);

        log.info("Successfully processed room booking - Room ID: {}, User: {}, Time: {} to {}",
                requestDto.getRoomId(),
                user.getUsername(),
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );
    }



    /**
     * Validates a room borrowing request.
     *
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
}