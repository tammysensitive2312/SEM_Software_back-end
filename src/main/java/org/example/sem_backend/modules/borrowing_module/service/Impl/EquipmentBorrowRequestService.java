package org.example.sem_backend.modules.borrowing_module.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentRequestDeniedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentReturnedEvent;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.*;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestDetailMapper;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestSpecification;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
import org.example.sem_backend.modules.borrowing_module.repository.detail.EquipmentBorrowRequestDetailRepository;
import org.example.sem_backend.modules.borrowing_module.service.InterfaceRequestService;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentBorrowRequestService implements InterfaceRequestService<EquipmentBorrowRequest, EquipmentBorrowRequestDTO> {
    private final UserRepository userRepository;
    private final EquipmentBorrowRequestRepository requestRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentBorrowRequestDetailRepository borrowRequestDetailRepository;
    private final EquipmentRepository equipmentRepository;
    private final TransactionsLogRepository logRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EquipmentBorrowRequestMapper requestMapper;
    private final EquipmentBorrowRequestDetailMapper detailMapper;
    /**
     * Xử lý yêu cầu mượn thiết bị từ người dùng.
     * Phương thức này thực hiện các công việc sau:
     * <ul>
     *   <li>Kiểm tra và lấy thông tin người dùng từ database</li>
     *   <li>Tạo mới một yêu cầu mượn thiết bị</li>
     *   <li>Xử lý từng thiết bị trong danh sách mượn</li>
     *   <li>Lưu thông tin yêu cầu mượn vào database</li>
     *   <li>Ghi log giao dịch</li>
     * </ul>
     *
     * @param requestDto Đối tượng DTO chứa thông tin yêu cầu mượn thiết bị, bao gồm:
     *
     * @throws ResourceConflictException Khi:
     *                                  <ul>
     *                                    <li>Không tìm thấy người dùng với ID đã cho</li>
     *                                    <li>Không tìm thấy thiết bị với mã đã cho</li>
     *                                  </ul>
     * @throws jakarta.persistence.OptimisticLockException Khi : xảy ra tranh chấp thiết bị cuối giữa nhiều user
     *
     */
    @Override
    @Transactional
    public void processRequest(EquipmentBorrowRequestDTO requestDto) {
//        System.out.println("Input dto : " + requestDto.toString());

        if (requestDto.getEquipmentItems() == null || requestDto.getEquipmentItems().isEmpty()) {
            throw new IllegalArgumentException("Equipment items cannot be null or empty");
        }

        validateRequest(requestDto);
        // Tìm kiếm người dùng
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceConflictException("User not found", "BORROWING-MODULE"));

        // Tạo mới đơn mượn
        EquipmentBorrowRequest request;
        request = requestMapper.toEntity(requestDto);
        request.setUser(user);

        // Xử lý từng loại thiết bị trong yêu cầu
        for (EquipmentBorrowItemDTO item : requestDto.getEquipmentItems()) {

            // Tìm kiếm loại thiết bị
            Equipment equipment = equipmentRepository.findByEquipmentName(item.getEquipmentName())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + item.getEquipmentName(), ""));

            // Tạo chi tiết đơn mượn (chưa gán EquipmentDetail)
            EquipmentBorrowRequestDetail detail = new EquipmentBorrowRequestDetail();
            detail.setBorrowRequest(request);
            detail.setEquipment(equipment);
            detail.setQuantityBorrowed(item.getQuantityBorrowed());
            detail.setConditionBeforeBorrow(item.getConditionBeforeBorrow());

            // Thêm vào danh sách chi tiết của đơn mượn
            request.getBorrowRequestDetails().add(detail);
        }

        // Lưu đơn mượn vào cơ sở dữ liệu
        requestRepository.save(request);

        // Ghi log giao dịch
        TransactionsLog transactionsLog = new TransactionsLog();
        transactionsLog.setEquipmentRequest(request);
        transactionsLog.setTransactionType("borrow equipment");
        transactionsLog.setUser(user);
        logRepository.save(transactionsLog);
    }


    /**
     * Validates a equipment borrowing request.
     *
     * @param requestDto @{@link RoomBorrowRequestDTO}
     * @throws ResourceConflictException if the booking start time is more than 2 weeks in the future
     *         or if there are conflicting bookings for the requested time slot.
     */
    @Override
    public boolean validateRequest(EquipmentBorrowRequestDTO requestDto) {
        // 1. Kiểm tra đơn quá hạn
        if (hasOverdueBorrowRequest(requestDto.getUserId())) {
            return false; // Có đơn quá hạn
        }
        // 2. Kiểm tra số lượng thực tế

        validateEquipmentAvailability(requestDto);
        return true; // Đơn hợp lệ
    }

    public boolean hasOverdueBorrowRequest(Long userId) {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);

        return requestRepository.hasOverdueRequests(
                userId,
                List.of(EquipmentBorrowRequest.Status.BORROWED, EquipmentBorrowRequest.Status.PARTIALLY_RETURNED),
                twoWeeksAgo
        );
    }

    public void validateEquipmentAvailability(EquipmentBorrowRequestDTO requestDto) {
        for (EquipmentBorrowItemDTO item : requestDto.getEquipmentItems()) {
            Equipment equipment = equipmentRepository.findByEquipmentName(
                    item.getEquipmentName())
                    .orElseThrow(() -> new ResourceConflictException(
                            "Equipment not found: " + item.getEquipmentName(), "BORROWING_MODULE"));

            if (equipment.getUsableQuantity() < item.getQuantityBorrowed()) {
                throw new ResourceConflictException(
                        "Not enough quantity for equipment: " + item.getEquipmentName(), "BORROWING_MODULE");
            }
        }
    }


    @Override
    public void updateRequest(EquipmentBorrowRequestDTO requestDto) {
        EquipmentBorrowRequest borrowRequest = requestRepository.findById(requestDto.getUniqueID())
                .orElseThrow(() -> new ResourceConflictException("Equipment Borrow Request not found", ""));

        // Kiểm tra trạng thái
        if (borrowRequest.getStatus() != EquipmentBorrowRequest.Status.NOT_BORROWED) {
            throw new IllegalStateException("Borrow request cannot be updated in its current state.");
        }

        // Kiểm tra thời gian hết hạn
        LocalDate now = LocalDate.now();
        if (now.isAfter(borrowRequest.getCreateAt().toLocalDate().plusDays(1))) {
            throw new IllegalStateException("Borrow request is overdue to update.");
        }

        borrowRequest.setExpectedReturnDate(requestDto.getExpectedReturnDate());
        borrowRequest.setComment(requestDto.getComment());
        // Tìm kiếm và cập nhật các chi tiết mượn
        List<EquipmentBorrowRequestDetail> newDetails = requestDto.getEquipmentItems().stream()
                .map(item -> {
                    // Tìm thiết bị trong cơ sở dữ liệu
                    Equipment equipment = equipmentRepository.findByEquipmentName(item.getEquipmentName())
                            .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + item.getEquipmentName(), ""));

                    // Ánh xạ DTO sang chi tiết
                    EquipmentBorrowRequestDetail detail = detailMapper.toEntity(item);
                    detail.setEquipment(equipment); // Đảm bảo thiết bị được tham chiếu từ DB
                    detail.setBorrowRequest(borrowRequest);
                    return detail;
                })
                .collect(Collectors.toList());

        // Xóa và thêm mới chi tiết
        borrowRequest.getBorrowRequestDetails().clear();
        borrowRequest.getBorrowRequestDetails().addAll(newDetails);

        // Lưu đơn mượn
        requestRepository.save(borrowRequest);
    }



    /**
     * Approves a borrow request by assigning specific equipment details to the request and
     * updating its status to "BORROWED". This method performs the following steps:
     * <ul>
     *   <li>Fetches the borrow request by its ID.</li>
     *   <li>Validates if the request is in a state eligible for approval.</li>
     *   <li>For each detail in the borrow request:
     *     <ul>
     *       <li>Fetches the available equipment details based on the equipment type and requested quantity.</li>
     *       <li>Validates if there are enough available equipment details.</li>
     *       <li>Assigns the fetched equipment details to the borrow request detail.</li>
     *     </ul>
     *   </li>
     *   <li>Updates the borrow request's status to "BORROWED".</li>
     *   <li>Publishes an event for other modules to handle related updates.</li>
     * </ul>
     *
     * @param requestId the ID of the borrow request to be approved.
     * @throws ResourceNotFoundException if the borrow request with the given ID does not exist.
     * @throws IllegalStateException if the borrow request is not in a state eligible for approval.
     * @throws ResourceConflictException if there are not enough available equipment details for any of the requested items.
     */
    @Transactional
    public void approveRequest(Long requestId) {
        EquipmentBorrowRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found", "BORROWING_MODULE"));

        if (request.getStatus() != EquipmentBorrowRequest.Status.NOT_BORROWED) {
            throw new IllegalStateException("Request is not in a state to be approved");
        }

        // Xử lý từng chi tiết đơn mượn
        for (EquipmentBorrowRequestDetail detail : request.getBorrowRequestDetails()) {
            Pageable pageable = PageRequest.of(0, detail.getQuantityBorrowed());
            List<EquipmentDetail> availableDetails = equipmentDetailRepository.findAvailableByEquipmentId(
                    detail.getEquipment().getId(), pageable);

            if (availableDetails.size() < detail.getQuantityBorrowed()) {
                throw new ResourceConflictException("Not enough available equipment for: " + detail.getEquipment().getEquipmentName(), "");
            }

            detail.getEquipmentDetails().addAll(availableDetails);
//            detailRepository.save(detail);
        }
        request.setStatus(EquipmentBorrowRequest.Status.BORROWED);
        requestRepository.save(request);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                eventPublisher.publishEvent(new EquipmentBorrowedEvent(this, request.getUniqueID(), request.getUser().getId()));
            }
        });
    }

    /**
     * Deletes multiple equipment borrow requests by their IDs.
     * This method performs the following steps:
     * <ul>
     *   <li>Validates the input list of request IDs</li>
     *   <li>Retrieves the corresponding EquipmentBorrowRequest entities</li>
     *   <li>Checks if each request is in a deletable state (NOT_BORROWED)</li>
     *   <li>Manually deletes associated borrow request details</li>
     *   <li>Deletes associated transaction logs</li>
     *   <li>Deletes the borrow requests</li>
     * </ul>
     *
     * @param requestIds A List of Long values representing the IDs of the equipment borrow requests to be deleted
     * @throws IllegalArgumentException if the requestIds list is null or empty
     * @throws ResourceNotFoundException if no EquipmentBorrowRequests are found for the given IDs
     * @throws IllegalStateException if any of the requests to be deleted is not in the NOT_BORROWED state
     */
    @Override
    public void deleteRequestsByIds(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            throw new IllegalArgumentException("Request IDs cannot be null or empty");
        }

        // Lưu trạng thái ban đầu của dữ liệu để khôi phục nếu xảy ra lỗi
        List<EquipmentBorrowRequest> originalRequests = requestRepository.findAllById(requestIds);

        if (originalRequests.isEmpty()) {
            throw new ResourceNotFoundException("No Equipment Borrow Requests found for the given IDs", "BORROWING_MODULE");
        }

        Map<Long, List<EquipmentBorrowRequestDetail>> originalDetails = new HashMap<>();
        for (EquipmentBorrowRequest request : originalRequests) {
            if (request.getStatus() != EquipmentBorrowRequest.Status.NOT_BORROWED) {
                throw new IllegalStateException(String.format(
                        "Cannot delete request with ID [%d] because it is already processed.", request.getUniqueID()));
            }
            originalDetails.put(request.getUniqueID(), new ArrayList<>(request.getBorrowRequestDetails()));
        }

        try {
            // Thực hiện xóa các chi tiết và yêu cầu
            for (EquipmentBorrowRequest request : originalRequests) {
                borrowRequestDetailRepository.deleteAll(request.getBorrowRequestDetails());
                request.getBorrowRequestDetails().clear();
            }

            logRepository.deleteByEquipmentRequestIds(requestIds);
            requestRepository.deleteAllInBatch(originalRequests);

        } catch (Exception e) {
            // Rollback: Khôi phục dữ liệu
            for (EquipmentBorrowRequest request : originalRequests) {
                List<EquipmentBorrowRequestDetail> backupDetails = originalDetails.get(request.getUniqueID());
                request.getBorrowRequestDetails().addAll(backupDetails);
                borrowRequestDetailRepository.saveAll(backupDetails);
            }
            // Ném lại ngoại lệ để thông báo lỗi
            throw new RuntimeException("Error occurred during deletion, changes have been rolled back.", e);
        }
    }


    public Page<EquipmentBorrowRequestSummaryDTO> getFilteredRequests(EquipmentBorrowRequestFilterDTO filterDTO, Pageable pageable) {
        Specification<EquipmentBorrowRequest> spec = Specification.where(null);

        if (filterDTO.getUserId() != null) {
            spec = spec.and(EquipmentBorrowRequestSpecification.hasUserId(filterDTO.getUserId()));
        }

        if (filterDTO.getStatuses() != null && !filterDTO.getStatuses().isEmpty()) {
            spec = spec.and(EquipmentBorrowRequestSpecification.hasStatuses(filterDTO.getStatuses()));
        }

        if (filterDTO.getExpectedReturnDateBefore() != null) {
            spec = spec.and(EquipmentBorrowRequestSpecification.expectedReturnDateBefore(filterDTO.getExpectedReturnDateBefore()));
        }

        if (filterDTO.getExpectedReturnDateAfter() != null) {
            spec = spec.and(EquipmentBorrowRequestSpecification.expectedReturnDateAfter(filterDTO.getExpectedReturnDateAfter()));
        }

        if (filterDTO.getUsername() != null && !filterDTO.getUsername().isEmpty()) {
            spec = spec.and(EquipmentBorrowRequestSpecification.userUsernameContains(filterDTO.getUsername()));
        }

        Page<EquipmentBorrowRequest> requests = requestRepository.findAll(spec, pageable);
        return requests.map(requestMapper::toSummaryDto);
    }

    public Page<EquipmentBorrowRequestSummaryDTO> getAllRequests(String filter, Pageable pageable) {
        Page<EquipmentBorrowRequest> requests;

        if (filter != null && !filter.isEmpty()) {
            requests = requestRepository.findByUser_UsernameContainingIgnoreCase(filter, pageable);
        } else {
            requests = requestRepository.findAll(pageable);
        }
        return requests.map(requestMapper::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public EquipmentBorrowRequestDetailsDTO getRequestDetails(Long requestId) {
        EquipmentBorrowRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow request not found for ID: " + requestId));

        List<EquipmentBorrowRequestDetailDTO> detailDTOs = request.getBorrowRequestDetails()
                .stream()
                .map(detailMapper::toDetailDto)
                .collect(Collectors.toList());

        return new EquipmentBorrowRequestDetailsDTO(request.getUniqueID(), detailDTOs);
    }

    @Transactional
    public void returnEquipment(List<Long> equipmentBorrowRequestIds) throws BadRequestException {
        if (equipmentBorrowRequestIds == null || equipmentBorrowRequestIds.isEmpty()) {
            throw new IllegalArgumentException("request id list should not be null or empty");
        }

        List<EquipmentBorrowRequest> borrowRequests = requestRepository
                .findAllById(equipmentBorrowRequestIds);

        if (borrowRequests.isEmpty()) {
            throw new ResourceNotFoundException("No borrow requests found", "EQUIPMENT-MODULE");
        }

        boolean hasInvalidStatus = borrowRequests.stream()
                .anyMatch(request -> request.getStatus() != EquipmentBorrowRequest.Status.BORROWED);
        if (hasInvalidStatus) {
            throw new BadRequestException("Some requests are not in BORROWED status");
        }

        borrowRequests.forEach(request -> {
//            boolean allReturned = request.getBorrowRequestDetails().stream()
//                    .allMatch(detail -> detail.getReturnedQuantity() >= detail.getBorrowedQuantity());
//
//            if (allReturned) {
//                request.setStatus(EquipmentBorrowRequest.Status.RETURNED);
//            } else {
//                request.setStatus(EquipmentBorrowRequest.Status.PARTIALLY_RETURNED);
//            }
            request.setStatus(EquipmentBorrowRequest.Status.RETURNED);
        });

        requestRepository.saveAll(borrowRequests);

        eventPublisher.publishEvent(new EquipmentReturnedEvent(this, equipmentBorrowRequestIds));
    }
    

    /**
     * Denies an equipment borrow request and updates its status to REJECTED.
     * This method also publishes an EquipmentRequestDeniedEvent
     *
     * @param denyDto An EquipmentBorrowRequestDenyDto object containing the request ID and denial reason.
     * @throws ResourceNotFoundException if the request with the given ID is not found.
     * @throws ResourceConflictException if the request is not in a state that can be rejected (i.e., not in NOT_BORROWED status).
     */
    public void denyRequest(EquipmentBorrowRequestDenyDto denyDto) {
        EquipmentBorrowRequest request = requestRepository.findById(denyDto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found", "BORROWING_MODULE"));

        if (request.getStatus() != EquipmentBorrowRequest.Status.NOT_BORROWED) {
            throw new ResourceConflictException("request is not in a state that can be REJECTED", "BORROWING_MODULE");
        }

        request.setStatus(EquipmentBorrowRequest.Status.REJECTED);
        requestRepository.save(request);

        Long userId = request.getUser().getId();
        String reason = denyDto.getReason();

        eventPublisher.publishEvent(new EquipmentRequestDeniedEvent(this, request.getUniqueID(), userId, reason));
    }

}
