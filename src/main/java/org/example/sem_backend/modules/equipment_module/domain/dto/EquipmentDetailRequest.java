package org.example.sem_backend.modules.equipment_module.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentDetailRequest {
    private String code;
    private String description;
    private String purchaseDate;
    private Integer roomId;
}
