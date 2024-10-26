package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEquipmentDetailService {
    Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(Long equipmentId, Pageable pageable);

    Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(Integer roomId, Pageable pageable);
}
