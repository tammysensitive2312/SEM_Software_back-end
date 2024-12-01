package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.equipment_module.enums.Category;

@Getter
@Setter
public class UpdateEquipmentRequest {
    private String equipmentName;
    private Category category;
    private String code;
}
