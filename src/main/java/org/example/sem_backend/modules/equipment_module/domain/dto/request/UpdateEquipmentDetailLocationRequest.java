package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateEquipmentDetailLocationRequest {
    private List<Long> equipmentDetailIds;
}
