package org.example.sem_backend.modules.room_module.service;

import org.example.sem_backend.modules.room_module.domain.dto.RoomDto;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    List<RoomDto> findAvailableRooms(String type, LocalDate date, String period);
    List<RoomDto> findRooms(Integer capacity, String comparisonOperator, String roomCondition);
}
