package org.example.sem_backend.modules.equipment_module.enums;

import lombok.Getter;

@Getter
public enum Category {
    TEACHING_EQUIPMENT("Thiết bị giảng dạy"),
    LABORATORY_EQUIPMENT("Thiết bị phòng thí nghiệm"),
    SPORTS_EQUIPMENT("Thiết bị thể thao"),
    INFORMATION_TECHNOLOGY_EQUIPMENT("Thiết bị công nghệ thông tin");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
