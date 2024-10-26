package org.example.sem_backend.modules.room_module.service;

import org.example.sem_backend.modules.room_module.domain.dto.RoomDto;

import java.time.LocalDate;
import java.util.List;

import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoomService {
    List<RoomDto> findAvailableRooms(String type, LocalDate date, String period);
    List<RoomDto> findRooms(Integer capacity, String comparisonOperator, String roomCondition);
    void addRoom(RoomDto request);

    void updateRoom(RoomDto request, int id);

    Page<RoomDto> filterRoomsByTypeAndStatus(RoomType type, RoomStatus status, Pageable pageable);

    //void deleteRoom(Long id);
}
