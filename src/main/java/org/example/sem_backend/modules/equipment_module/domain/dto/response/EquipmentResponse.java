package org.example.sem_backend.modules.equipment_module.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class EquipmentResponse {
    private long id;
    private String equipmentName;
    private String code;
    private String category;
    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;
    private int inUseQuantity;
    private int totalQuantityHasUsableInWarehouse;
}
