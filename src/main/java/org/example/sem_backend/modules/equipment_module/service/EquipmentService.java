package org.example.sem_backend.modules.equipment_module.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.CreateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
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

import java.util.List;
import java.util.Optional;
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
    public void addEquipment(CreateEquipmentRequest request) {
        // update code
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId(), "EQUIPMENT-MODULE"));

        // Sử dụng Optional để tìm Equipment nếu tồn tại, hoặc tạo mới nếu không tìm thấy
        Equipment existingEquipment = Optional.ofNullable(equipmentRepository.findByEquipmentName(request.getEquipmentName()))
                .orElseGet(() -> equipmentRepository.save(
                        Equipment.builder()
                                .equipmentName(request.getEquipmentName())
                                .category(request.getCategory())
                                .code(request.getCode())
                                .build()
                ));

        long count = equipmentDetailRepository.countByEquipment(existingEquipment);

        // Sinh số sê-ri: mã code nối với số thứ tự
        String serialNumber = request.getCode() + "-" + (count + 1);

        // Sử dụng Builder Pattern để xây dựng EquipmentDetail
        EquipmentDetail equipmentDetail = EquipmentDetail.builder()
                .equipment(existingEquipment)
                .serialNumber(serialNumber)
                .description(request.getDescription())
                .purchaseDate(request.getPurchaseDate())
                .status(EquipmentDetailStatus.USABLE)
                .room(room)
                .build();

        // Lưu EquipmentDetail vào repository
        equipmentDetailRepository.save(equipmentDetail);

        // Cập nhật số lượng của Equipment
        existingEquipment.setTotalQuantity(existingEquipment.getTotalQuantity() + 1);
        existingEquipment.setUsableQuantity(existingEquipment.getUsableQuantity() + 1);
        equipmentRepository.save(existingEquipment);
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