package org.example.sem_backend.modules.room_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.room_module.domain.dto.request.RoomFilterRequest;
import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.entity.RoomStatus;
import org.example.sem_backend.modules.room_module.domain.entity.RoomType;
import org.example.sem_backend.modules.room_module.domain.mapper.RoomMapper;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService{

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public void addRoom(RoomRequest request) {
        Room room = roomMapper.toEntity(request);
        roomRepository.save(room);
    }

    @Override
    public void updateRoom(RoomRequest request, Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setRoomName(request.getRoomName());
        room.setType(request.getType());
        room.setStatus(request.getStatus());
        room.setCapacity(request.getCapacity());
        roomRepository.save(room);
    }

    @Override
    public Page<RoomResponse> filterRoomsByTypeAndStatus(RoomType type, RoomStatus status, Pageable pageable) {
        String typeStr = type != null ? type.name() : null;
        String statusStr = status != null ? status.name() : null;

        Page<Room> roomPage = roomRepository.findByTypeAndStatus(typeStr, statusStr, pageable);
        return roomPage.map(roomMapper::toResponse);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        roomRepository.delete(room);
    }
}
