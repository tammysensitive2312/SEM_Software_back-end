package org.example.sem_backend.modules.room_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRoomDto {
    private String number;
    private String type;
    private int capacity;
    private String roomCondition;
    private String availableFrom;
    private String availableTo;
}
