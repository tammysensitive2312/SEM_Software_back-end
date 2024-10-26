package org.example.sem_backend.modules.room_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    // một dto handle cho cả request và response
    private String description;
    private String type;
    private int capacity;
    private RoomStatus status;
}
