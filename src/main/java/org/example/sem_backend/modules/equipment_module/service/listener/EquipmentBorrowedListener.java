package org.example.sem_backend.modules.equipment_module.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class EquipmentBorrowedListener {

    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentBorrowRequestRepository borrowRequestRepository;

    public EquipmentBorrowedListener(EquipmentDetailRepository equipmentDetailRepository,
                                     EquipmentBorrowRequestRepository borrowRequestRepository) {
        this.equipmentDetailRepository = equipmentDetailRepository;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @EventListener
    @Transactional
    @Async// Đảm bảo xử lý trong một transaction
    public void onEquipmentBorrowed(EquipmentBorrowedEvent event) {
        log.info("Equipment borrowed - Request ID: {}, User ID: {}",
                event.getRequestId(),
                event.getUserId());

        // 1. Truy xuất thông tin từ EquipmentBorrowRequest
        EquipmentBorrowRequest request = borrowRequestRepository.findById(event.getRequestId())
                .orElseThrow(() -> new IllegalStateException("Borrow request not found"));

        // 2. Xử lý từng chi tiết đơn mượn
        for (EquipmentBorrowRequestDetail detail : request.getBorrowRequestDetails()) {
            detail.getEquipment().setInUseQuantity(detail.getQuantityBorrowed());

            for (EquipmentDetail equipmentDetail : detail.getEquipmentDetails()) {
                // 3. Cập nhật trạng thái của từng EquipmentDetail
                equipmentDetail.setStatus(EquipmentDetailStatus.OCCUPIED);
                equipmentDetailRepository.save(equipmentDetail); // Lưu trạng thái mới
            }
        }

        log.info("All EquipmentDetails for Request ID: {} updated successfully", event.getRequestId());
    }
}
