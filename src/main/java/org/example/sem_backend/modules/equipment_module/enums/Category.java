package org.example.sem_backend.modules.equipment_module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    TEACHING_EQUIPMENT("Thiết bị giảng dạy"),
    LABORATORY_EQUIPMENT("Thiết bị phòng thí nghiệm"),
    SPORTS_EQUIPMENT("Thiết bị thể thao"),
    INFORMATION_TECHNOLOGY_EQUIPMENT("Thiết bị công nghệ thông tin"),
    ELECTRIC_EQUIPMENT("Thiết bị điện"),
    OFFICE_EQUIPMENT("Thiết bị văn phòng");

    private final String description;
}
