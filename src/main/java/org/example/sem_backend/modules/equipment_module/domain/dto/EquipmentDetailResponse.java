package org.example.sem_backend.modules.equipment_module.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentDetailResponse {
    private Long id;
    private String equipmentName;
    private String purchaseDate;
    private String description;
    private String code;
    private String status;
    private String category;
    private int operatingHours;
    private String roomName;
}
