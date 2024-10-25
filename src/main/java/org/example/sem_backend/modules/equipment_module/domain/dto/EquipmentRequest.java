package org.example.sem_backend.modules.equipment_module.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.equipment_module.domain.entity.Category;

@Getter
@Setter
public class EquipmentRequest {
    private String equipmentName;
    private Category category;
    private String code;
    private String description;
    private String purchaseDate;
    private Integer roomId;
}