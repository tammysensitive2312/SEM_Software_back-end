package org.example.sem_backend.modules.room_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private String description;
    private String type;
    private int capacity;
    private String roomCondition;
}
