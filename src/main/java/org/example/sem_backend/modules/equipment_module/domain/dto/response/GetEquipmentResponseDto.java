package org.example.sem_backend.modules.equipment_module.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEquipmentResponseDto {
    private String equipmentName;
    private String purchaseDate;
    private String roomDescription;
    private String equipmentCategory;
    private String code;
    private String description;
    private String currentStatus;
    private int operatingHours;
}
