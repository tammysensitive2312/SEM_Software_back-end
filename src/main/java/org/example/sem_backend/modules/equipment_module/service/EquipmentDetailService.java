package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentDetailMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentDetailService implements IEquipmentDetailService {
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentDetailMapper equipmentDetailMapper;

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

}
