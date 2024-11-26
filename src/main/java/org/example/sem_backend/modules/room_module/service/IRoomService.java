package org.example.sem_backend.modules.room_module.service;

import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.RoomStatus;
import org.example.sem_backend.modules.room_module.domain.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    void addRoom(RoomRequest request);
    List<RoomResponse> findAvailableRooms(String type, LocalDate date, String period);

    List<RoomResponse> searchRoom(String keyword);

    List<RoomResponse> findRooms(Integer capacity, String comparisonOperator, String roomCondition);

    void updateRoom(RoomRequest request, Integer id);

    Page<RoomResponse> filterRoomsByTypeAndStatus(RoomType type, RoomStatus status, Pageable pageable);
}
