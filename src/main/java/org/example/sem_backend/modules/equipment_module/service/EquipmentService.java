package org.example.sem_backend.modules.equipment_module.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService implements IEquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentDetailService equipmentDetailService;

    @Override
    @Transactional
    public void addEquipment(EquipmentRequest request) {
        if (equipmentRepository.existsByCode(request.getCode())) {
            throw new ResourceNotFoundException("Equipment with code " + request.getCode() + " already exists", "EQUIPMENT-MODULE");
        }
        Equipment equipment = equipmentMapper.toEquipment(request);
        equipmentRepository.save(equipment);
    }

    @Override
    public void updateEquipment(Long id, UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id, "EQUIPMENT-MODULE"));

        // Cập nhật thông tin thiết bị
        equipment.setEquipmentName(request.getEquipmentName());
        equipment.setCategory(request.getCategory());
        equipment.setCode(request.getCode());
        equipmentRepository.save(equipment);
    }

    @Override
    public Page<EquipmentResponse> searchEquipments(String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Equipment> equipments = equipmentRepository.searchEquipment(category, keyword, pageable);

        return equipments.map(equipment -> {
            int totalUsableInWarehouse = (int) equipmentDetailService.countUsableEquipmentInWarehouseByEquipmentId(equipment.getId());
            return EquipmentResponse.builder()
                    .id(equipment.getId())
                    .equipmentName(equipment.getEquipmentName())
                    .code(equipment.getCode())
                    .category(equipment.getCategory().getDescription())
                    .totalQuantity(equipment.getTotalQuantity())
                    .usableQuantity(equipment.getUsableQuantity())
                    .brokenQuantity(equipment.getBrokenQuantity())
                    .inUseQuantity(equipment.getInUseQuantity())
                    .totalQuantityHasUsableInWarehouse(totalUsableInWarehouse)
                    .build();
        });
    }

}