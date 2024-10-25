package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IEquipmentService {
    Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(Pageable pageable);
}
