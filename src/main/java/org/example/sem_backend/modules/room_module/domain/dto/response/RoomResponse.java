package org.example.sem_backend.modules.room_module.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {
    private Long id;
    private String roomName;
    private String type;
    private String status;
    private int capacity;
}
