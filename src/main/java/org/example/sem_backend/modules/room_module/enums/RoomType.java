package org.example.sem_backend.modules.room_module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {
    Classroom("phòng học"),
    Laboratory("phòng thí nghiệm"),
    Conference("phòng hội thảo"),
    Warehouse("phòng kho thiết bị"),
    Office("văn phòng cán bộ");

    private final String description;
}
