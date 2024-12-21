package org.example.sem_backend.modules.equipment_module.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentReturnedEvent;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public void onEquipmentBorrowed(EquipmentBorrowedEvent event) {
        try {
            EquipmentBorrowRequest request = borrowRequestRepository.findById(event.getRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "not found request with id : " + event.getRequestId(), "Equipment-Module")
                    );

            processEquipmentBorrowRequest(request);

            log.info("Successfully processed equipment borrow request - Request ID: {}", event.getRequestId());
        } catch (ResourceNotFoundException ex) {
            log.error("Equipment borrow request not found - Request ID: {}", event.getRequestId(), ex);
        } catch (Exception ex) {
            log.error("Error processing equipment borrow event - Request ID: {}", event.getRequestId(), ex);
        }
    }

    private void processEquipmentBorrowRequest(EquipmentBorrowRequest request) {
        List<EquipmentDetail> updatedEquipmentDetails = request.getBorrowRequestDetails().stream()
                .flatMap(detail -> {
                    Equipment equipment = detail.getEquipment();
                    equipment.setInUseQuantity(equipment.getInUseQuantity() + detail.getQuantityBorrowed());

                    return detail.getEquipmentDetails().stream()
                            .peek(equipmentDetail -> equipmentDetail.setStatus(EquipmentDetailStatus.OCCUPIED));
                })
                .collect(Collectors.toList());

        equipmentDetailRepository.saveAll(updatedEquipmentDetails);
    }

    @EventListener
    @Transactional
    public void onEquipmentReturned(EquipmentReturnedEvent event) {
        try {
            List<EquipmentBorrowRequest> requestList = borrowRequestRepository.findAllById(event.getRequestId());

            if (requestList.isEmpty()) {
                throw new ResourceNotFoundException("No borrow requests found", "EQUIPMENT-MODULE");
            }

            requestList.forEach(this::processEquipmentReturned);

        } catch (ResourceNotFoundException ex) {
            log.error("Equipment borrow request not found - Request ID: {}", event.getRequestId(), ex);
        } catch (Exception ex) {
            log.error("Error processing equipment borrow event - Request ID: {}", event.getRequestId(), ex);
        }
    }

    private void processEquipmentReturned(EquipmentBorrowRequest request) {

        List<EquipmentDetail> updatedDetails = request.getBorrowRequestDetails().stream()
                .flatMap(detail -> {
                    Equipment equipment = detail.getEquipment();
                    equipment.setInUseQuantity(equipment.getInUseQuantity() - detail.getQuantityBorrowed());

                    return detail.getEquipmentDetails().stream()
                            .peek(equipmentDetail -> equipmentDetail.setStatus(EquipmentDetailStatus.USABLE));
                })
                .collect(Collectors.toList());

        equipmentDetailRepository.saveAllAndFlush(updatedDetails);
    }
}
