package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentDetailMapper;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentDetailService implements IEquipmentDetailService {
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentDetailMapper equipmentDetailMapper;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional
    public void addEquipmentDetail(EquipmentDetailRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId(), "EQUIPMENT-DETAIL-MODULE"));
        Equipment existingEquipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + request.getEquipmentId(), "EQUIPMENT-DETAIL-MODULE"));

        long count = equipmentDetailRepository.countByEquipment(existingEquipment);

        // Sinh số sê-ri: mã code nối với số thứ tự
        String serialNumber = existingEquipment.getCode() + "-" + (count + 1);

        if (equipmentDetailRepository.existsBySerialNumber(serialNumber)) {
            throw new ResourceConflictException("Serial number " + serialNumber + " already exists", "EQUIPMENT-DETAIL-MODULE");
        }

        EquipmentDetail equipmentDetail = equipmentDetailMapper.toEntity(request);
        equipmentDetail.setStatus(EquipmentDetailStatus.USABLE);
        equipmentDetail.setSerialNumber(serialNumber);
        equipmentDetail.setEquipment(existingEquipment);
        equipmentDetail.setRoom(room);

        equipmentDetailRepository.save(equipmentDetail);

        existingEquipment.incrementQuantity();
        equipmentRepository.save(existingEquipment);
    }

    @Override
    public void updateEquipmentDetail(Long id, EquipmentDetailRequest request) {
        EquipmentDetail equipmentDetail = equipmentDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentDetail not found with ID: " + request.getEquipmentId(), "EQUIPMENT-DETAIL-MODULE"));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId(), "EQUIPMENT-DETAIL-MODULE"));
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + request.getEquipmentId(), "EQUIPMENT-DETAIL-MODULE"));
        equipmentDetail.setDescription(request.getDescription());
        equipmentDetail.setPurchaseDate(request.getPurchaseDate());
        equipmentDetail.setRoom(room);
        equipmentDetail.setEquipment(equipment);
        equipmentDetailRepository.save(equipmentDetail);
    }

    /**
     * Retrieves a paginated list of equipment details sorted by room number in ascending order.
     *
     * @param pageable The pagination and sorting parameters.
     * @return A paginated list of {@link EquipmentResponse} objects representing the equipment details.
     * @throws ResourceNotFoundException If no equipment details are found.
     */
    public Page<EquipmentDetailResponse> getAllEquipmentSortedByRoom(Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findAllByOrderByRoomAsc(pageable);
        if (equipmentDetails.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thiết bị nào.", "EQUIPMENT_MODULE");
        }
        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }

    @Override
    public void updateEquipmentDetailLocation(List<Long> equipmentDetailIds, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId, "ROOM_MODULE"));
        List<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findAllById(equipmentDetailIds);

        if (equipmentDetails.isEmpty()) {
            throw new ResourceNotFoundException("No equipment details found for the provided IDs", "EQUIPMENT-DETAIL_MODULE");
        }
        // Update room for each equipment detail
        for (EquipmentDetail equipmentDetail : equipmentDetails) {
            equipmentDetail.setRoom(room);
        }
        // Save all changes
        equipmentDetailRepository.saveAll(equipmentDetails);
    }

    @Override
    public Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(Long equipmentId, int page, int size) {
        // Tạo Pageable với sắp xếp giảm dần theo id
        Pageable pageableSort = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // Gọi repository với Pageable đã được sắp xếp
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findByEquipmentId(equipmentId, pageableSort);

        // Ánh xạ kết quả sang EquipmentDetailResponse
        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }


    @Override
    public Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(Integer roomId, Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findByRoomId(roomId, pageable);

        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }

    @Override
    public List<EquipmentDetailResponse> searchEquipmentDetail(String keyword) {
        List<EquipmentDetail> equipmentDetails = equipmentDetailRepository.searchEquipmentDetail(keyword);
        if (equipmentDetails.isEmpty()) {
            throw new ResourceNotFoundException("No equipment detail found with keyword: " + keyword, "EQUIPMENT-DETAIL_MODULE");
        }
        return equipmentDetails.stream()
                .map(equipmentDetailMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEquipmentDetail(Long equipmentDetailId) {
        EquipmentDetail equipmentDetail = equipmentDetailRepository.findById(equipmentDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentDetail not found with ID: " + equipmentDetailId, "EQUIPMENT-DETAIL-MODULE"));

        Equipment equipment = equipmentDetail.getEquipment();

        // Cập nhật số lượng dựa trên trạng thái
        equipment.setTotalQuantity(equipment.getTotalQuantity() - 1);

        if (equipmentDetail.getStatus() == EquipmentDetailStatus.USABLE) {
            equipment.setUsableQuantity(equipment.getUsableQuantity() - 1);
        } else if (equipmentDetail.getStatus() == EquipmentDetailStatus.BROKEN) {
            equipment.setBrokenQuantity(equipment.getBrokenQuantity() - 1);
        }

        // Kiểm tra tính hợp lệ
        if (equipment.getTotalQuantity() < 0 || equipment.getUsableQuantity() < 0 || equipment.getBrokenQuantity() < 0) {
            throw new IllegalStateException("Equipment quantities cannot be negative");
        }

        // Xóa EquipmentDetail
        equipmentDetailRepository.delete(equipmentDetail);

        // Lưu cập nhật vào thiết bị
        equipmentRepository.save(equipment);
    }
}