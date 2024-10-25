package org.example.sem_backend.modules.equipment_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEquipmentResponseDto {
    private String roomDescription;
    private String roomType;
    private String equipmentCategory;
    private String code;
    private String description;
    private String currentStatus;
    private int operatingHours;
}
