package org.example.sem_backend.modules.room_module.domain.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.room_module.domain.entity.RoomStatus;
import org.example.sem_backend.modules.room_module.domain.entity.RoomType;

@Getter
@Setter
public class RoomFilterRequest {
    private RoomType type;
    private RoomStatus status;
}
