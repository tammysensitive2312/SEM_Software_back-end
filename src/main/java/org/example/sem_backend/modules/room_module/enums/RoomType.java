package org.example.sem_backend.modules.room_module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomType {
    CLASSROOM("phòng học"),
    LABORATORY("phòng thí nghiệm"),
    MEETING_ROOM("phòng hội thảo"),
    WAREHOUSE("phòng kho thiết bị"),
    OFFICE("văn phòng cán bộ");

    private final String description;
}
