package org.example.sem_backend.modules.equipment_module.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EquipmentResponse {
    private Long id;
    private String equipmentName;
    private String code;
    private String category;
    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;
}
