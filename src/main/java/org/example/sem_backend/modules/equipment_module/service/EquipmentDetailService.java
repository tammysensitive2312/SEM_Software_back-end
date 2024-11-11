package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sem_backend.main_service.middleware.LoggingFilter;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentDetailMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentDetailService implements IEquipmentDetailService {
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentDetailMapper equipmentDetailMapper;
    private static final Logger logger = LogManager.getLogger(LoggingFilter.class);
    @Override
    public Page<GetEquipmentResponseDto> getEquipmentDetailsByEquipmentId(Long equipmentId, Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findByEquipmentId(equipmentId, pageable);
        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }

    @Override
    public Page<GetEquipmentResponseDto> getEquipmentDetailsByRoomId(Long roomId, Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findByRoom_UniqueId(roomId, pageable);
        return equipmentDetails.map(equipmentDetailMapper::toResponse);
    }

}
