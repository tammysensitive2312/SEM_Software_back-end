package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.sem_backend.modules.equipment_module.enums.Category;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEquipmentRequest {
    private String equipmentName;
    private Category category;
    private String code;
    private String description;
    private String purchaseDate;
    private Long roomId;
}
