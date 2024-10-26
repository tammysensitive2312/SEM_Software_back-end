package org.example.sem_backend.modules.equipment_module.domain.dto;

import lombok.Getter;
import org.example.sem_backend.modules.equipment_module.domain.entity.Category;

@Getter
public class UpdateEquipmentRequest {
    private String equipmentName;
    private Category category;
}
