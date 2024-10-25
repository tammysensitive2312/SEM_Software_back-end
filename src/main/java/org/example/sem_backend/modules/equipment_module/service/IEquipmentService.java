package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.dto.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IEquipmentService {
    void addEquipment(EquipmentRequest request);

    void updateEquipment(Long id, UpdateEquipmentRequest request);

    Page<EquipmentResponse> getEquipmentsByCategory(Category category, Pageable pageable);
}
