package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEquipmentDetailService {
    Page<GetEquipmentResponseDto> getEquipmentDetailsByEquipmentId(Long equipmentId, Pageable pageable);

    Page<GetEquipmentResponseDto> getEquipmentDetailsByRoomId(Long roomId, Pageable pageable);
}
