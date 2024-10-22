package org.example.sem_backend.modules.room_module.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {
    CLASSROOM("Phòng học"),
    EQUIPMENT_ROOM("Phòng thiết bị"),
    MEETING_ROOM("Phòng họp"),
    LABORATORY("Phòng thí nghiệm"),
    OFFICE("Văn phòng");

    private final String value;
}
