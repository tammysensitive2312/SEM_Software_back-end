package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentDetailMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentDetailService implements IEquipmentDetailService {
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentDetailMapper equipmentDetailMapper;
    private final RoomRepository roomRepository;

    @Override
    public void updateEquipmentDetailLocation(Long equipmentDetailId, Integer roomId) {
        EquipmentDetail equipmentDetail = equipmentDetailRepository.findById(equipmentDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment detail with equipment id " + equipmentDetailId, "EQUIPMENT-DETAIL_MODULE"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + roomId, "ROOM_MODULE"));

        equipmentDetail.setRoom(room);
        equipmentDetailRepository.save(equipmentDetail);
    }

    @Override
    public Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(Long equipmentId, Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findByEquipmentId(equipmentId, pageable);
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
    public void deleteEquipmentDetail(Long id) {
        EquipmentDetail equipmentDetail = equipmentDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment detail with id " + id + " not found", "EQUIPMENT-DETAIL_MODULE"));
        equipmentDetailRepository.deleteById(equipmentDetail.getId());
    }
}
