package org.example.sem_backend.modules.room_module.domain.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.room_module.enums.RoomType;

@Setter
@Getter
public class RoomRequest {
    private String roomName;
    private RoomType type;
    private Integer capacity;
}