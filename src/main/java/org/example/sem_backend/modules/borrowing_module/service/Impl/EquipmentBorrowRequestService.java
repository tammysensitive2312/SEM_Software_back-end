package org.example.sem_backend.modules.borrowing_module.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowItemDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDetailsDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestSummaryDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestDetailMapper;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentBorrowRequestService implements InterfaceRequestService<EquipmentBorrowRequest, EquipmentBorrowRequestDTO> {
    private final UserRepository userRepository;
    private final EquipmentBorrowRequestRepository requestRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
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
        // Kiểm tra danh sách thiết bị có null hay không
        if (requestDto.getEquipmentItems() == null || requestDto.getEquipmentItems().isEmpty()) {
            throw new IllegalArgumentException("Equipment items cannot be null or empty");
        }

        validateRequest(requestDto);
        // Tìm kiếm người dùng
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceConflictException("User not found", "BORROWING-MODULE"));

        // Tạo mới đơn mượn
        EquipmentBorrowRequest request = new EquipmentBorrowRequest();
        request.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
        requestMapper.toEntity(requestDto);

        // Xử lý từng loại thiết bị trong yêu cầu
        for (EquipmentBorrowItemDTO item : requestDto.getEquipmentItems()) {

            // Tìm kiếm loại thiết bị
            Equipment equipment = equipmentRepository.findEquipmentByName(item.getEquipmentName())
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
            Equipment equipment = equipmentRepository.findEquipmentByName(
                    item.getEquipmentName())
                    .orElseThrow(() -> new ResourceConflictException(
                            "Equipment not found: " + item.getEquipmentName(), "BORROWING_MODULE"));

            if (equipment.getUsableQuantity() < item.getQuantityBorrowed()) {
                throw new ResourceConflictException(
                        "Not enough quantity for equipment: " + item.getEquipmentName(), "BORROWING_MODULE");
            }
        }
    }



    /**
     * @param requestDto
     * @return
     */
    @Override
    public EquipmentBorrowRequest updateRequest(EquipmentBorrowRequestDTO requestDto) {
        return null;
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
        // Tìm kiếm đơn mượn
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
                throw new ResourceConflictException("Not enough available equipment for: " + detail.getEquipment().getName(), "");
            }

            // Gán các thiết bị cụ thể
            detail.getEquipmentDetails().addAll(availableDetails);
//            detailRepository.save(detail);
        }
        request.setStatus(EquipmentBorrowRequest.Status.BORROWED);
        requestRepository.save(request);

        eventPublisher.publishEvent(new EquipmentBorrowedEvent(this, request.getUniqueID(), request.getUser().getId()));
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

        return detailMapper.toDetailsDto(request);
    }

}
