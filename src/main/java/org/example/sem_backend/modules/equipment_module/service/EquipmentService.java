package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.entity.Category;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService implements IEquipmentService {



    private final EquipmentRepository equipmentRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final RoomRepository roomRepository;
    private final EquipmentMapper equipmentMapper;

    @Override
    public void addEquipment(EquipmentRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

        Equipment existingEquipment = equipmentRepository.findByEquipmentName(request.getEquipmentName());
        System.out.println("existingEquipment" + existingEquipment);

        if (existingEquipment == null) {
            Equipment equipment = new Equipment();
            equipment.setEquipmentName(request.getEquipmentName());
            equipment.setCategory(request.getCategory());
            equipmentRepository.save(equipment);
        }


        if (equipmentDetailRepository.existsByCode(request.getCode())) {
            throw new ResourceConflictException("Equipment code already exists");
        }

        EquipmentDetail equipmentDetail = new EquipmentDetail();
        equipmentDetail.setEquipment(existingEquipment);
        equipmentDetail.setCode(request.getCode());
        equipmentDetail.setDescription(request.getDescription());
        equipmentDetail.setPurchaseDate(request.getPurchaseDate());
        equipmentDetail.setStatus(EquipmentDetailStatus.USABLE);
        equipmentDetail.setRoom(room);

        equipmentDetailRepository.save(equipmentDetail);

        assert existingEquipment != null;
        existingEquipment.setTotalQuantity(existingEquipment.getTotalQuantity() + 1);
        existingEquipment.setUsableQuantity(existingEquipment.getUsableQuantity() + 1);
        equipmentRepository.save(existingEquipment);
    }

    @Override
    public void updateEquipment(Long id, UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));

        // Cập nhật thông tin thiết bị
        equipment.setEquipmentName(request.getEquipmentName());
        equipment.setCategory(request.getCategory());
        equipmentRepository.save(equipment);
    }

    @Override
    public Page<EquipmentResponse> getEquipmentsByCategory(Category category, Pageable pageable) {
        String categoryStr = category != null ? category.name() : null;

        Page<Equipment> equipments = equipmentRepository.findByCategory(categoryStr, pageable);
        return equipments.map(equipmentMapper::toEquipmentResponse);
    }


}
