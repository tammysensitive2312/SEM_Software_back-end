package org.example.sem_backend.modules.room_module.service;

import org.example.sem_backend.modules.room_module.domain.dto.AvailableRoomDto;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    List<AvailableRoomDto> findAvailableRooms(String type, LocalDate date, String period);
}
