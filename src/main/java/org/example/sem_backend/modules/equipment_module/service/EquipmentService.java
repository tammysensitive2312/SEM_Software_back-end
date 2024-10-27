package org.example.sem_backend.modules.equipment_module.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.CreateEquipmentRequest;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;

import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;

import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentDetailMapper;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentMapper;
import org.example.sem_backend.modules.equipment_module.enums.Category;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EquipmentService implements IEquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final RoomRepository roomRepository;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentDetailMapper equipmentDetailMapper;

    /**
    * Retrieves a paginated list of equipment details sorted by room number in ascending order.
    *
    * @param pageable The pagination and sorting parameters.
    * @return A paginated list of {@link GetEquipmentResponseDto} objects representing the equipment details.
    * @throws ResourceNotFoundException If no equipment details are found.
    */
    @Override
    public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findAllByOrderByRoomAsc(pageable);
        if (equipmentDetails.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thiết bị nào.", "EQUIPMENT_MODULE");
        }
        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }
    @Override
    @Async
    @Transactional
    public CompletableFuture<Void> addEquipment(CreateEquipmentRequest request) {
        return CompletableFuture.runAsync(() -> {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId(), "EQUIPMENT-MODULE"));

            // Sử dụng Optional để tìm Equipment nếu tồn tại, hoặc tạo mới nếu không tìm thấy
            Equipment existingEquipment = Optional.ofNullable(equipmentRepository.findEquipmentByName(request.getEquipmentName()))
                    .orElseGet(() -> equipmentRepository.save(
                            Equipment.builder()
                                    .name(request.getEquipmentName())
                                    .category(request.getCategory())
                                    .build()
                    ));

            // Kiểm tra mã thiết bị trong EquipmentDetail
            if (equipmentDetailRepository.existsByCode(request.getCode())) {
                throw new ResourceConflictException("Equipment code already exists", "EQUIPMENT-MODULE");
            }

            // Sử dụng Builder Pattern để xây dựng EquipmentDetail
            EquipmentDetail equipmentDetail = EquipmentDetail.builder()
                    .equipment(existingEquipment)
                    .code(request.getCode())
                    .description(request.getDescription())
                    .purchaseDate(request.getPurchaseDate())
                    .status(EquipmentDetailStatus.USABLE)
                    .room(room)
                    .operatingHours(0)
                    .build();

            // Lưu EquipmentDetail vào repository
            equipmentDetailRepository.save(equipmentDetail);

            // Cập nhật số lượng của Equipment
            existingEquipment.setTotalQuantity(existingEquipment.getTotalQuantity() + 1);
            existingEquipment.setUsableQuantity(existingEquipment.getUsableQuantity() + 1);
            equipmentRepository.save(existingEquipment);
        });
    }



    @Override
    public void updateEquipment(Long id, UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id, "EQUIPMENT-MODULE"));

        // Cập nhật thông tin thiết bị
        equipment.setName(request.getEquipmentName());
        equipment.setCategory(request.getCategory());
        equipmentRepository.save(equipment);
    }

    /**
     * @param category
     * @param pageable
     * @return
     */

    @Override
    public Page<EquipmentResponse> getEquipmentsByCategory(Category category, Pageable pageable) {
        String categoryStr = category != null ? category.name() : null;

        Page<Equipment> equipments = equipmentRepository.findByCategory(categoryStr, pageable);
        return equipments.map(equipmentMapper::toEquipmentResponse);
    }


}
