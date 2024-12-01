package org.example.sem_backend.modules.equipment_module.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentMapper;
import org.example.sem_backend.modules.equipment_module.enums.Category;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService implements IEquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final RoomRepository roomRepository;
    private final EquipmentMapper equipmentMapper;

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
    public Page<EquipmentResponse> filterEquipment(Category category, Pageable pageable) {
        String categoryStr = category != null ? category.name() : null;

        Page<Equipment> equipments = equipmentRepository.findByCategory(categoryStr, pageable);
        return equipments.map(equipmentMapper::toEquipmentResponse);
    }

    @Override
    public List<EquipmentResponse> searchEquipments(String keyword) {
        List<Equipment> equipments = equipmentRepository.searchEquipment(keyword);
        if (equipments.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thiết bị nào", "EQUIPMENT-MODULE");
        }
        return equipments.stream()
                .map(equipmentMapper::toEquipmentResponse)
                .collect(Collectors.toList());
    }
}