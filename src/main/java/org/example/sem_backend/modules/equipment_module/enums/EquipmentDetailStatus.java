package org.example.sem_backend.modules.equipment_module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentDetailStatus {
    USABLE("Có thể sử dụng"),
    BROKEN("Hỏng"),
    OCCUPIED("Đang sử dụng");

    private final String value;
}
