package org.example.sem_backend.modules.room_module.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {
    Classroom("Phòng học"),
    EQUIPMENT_ROOM("Phòng thiết bị"),
    Conference("Phòng họp"),
    Laboratory("Phòng thí nghiệm"),
    Office("Văn phòng");

    private final String value;
}
