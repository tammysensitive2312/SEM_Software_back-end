package org.example.sem_backend.modules.room_module.service;

import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.RoomStatus;
import org.example.sem_backend.modules.room_module.domain.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoomService {
    void addRoom(RoomRequest request);

    void updateRoom(RoomRequest request, Long id);

    Page<RoomResponse> filterRoomsByTypeAndStatus(RoomType type, RoomStatus status, Pageable pageable);

    void deleteRoom(Long id);
}
