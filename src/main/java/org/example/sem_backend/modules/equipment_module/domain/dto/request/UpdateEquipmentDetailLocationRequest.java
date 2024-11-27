package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEquipmentDetailLocationRequest {
    private Long equipmentDetailId;
    private Integer roomId;
}
