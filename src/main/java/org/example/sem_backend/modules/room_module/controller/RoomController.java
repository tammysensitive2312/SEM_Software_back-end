package org.example.sem_backend.modules.room_module.controller;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.RoomStatus;
import org.example.sem_backend.modules.room_module.domain.entity.RoomType;
import org.example.sem_backend.modules.room_module.service.IRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private final IRoomService roomService;

    @PostMapping
    public ResponseEntity<String> addRoom(@RequestBody RoomRequest roomRequest) {
        roomService.addRoom(roomRequest);
        return ResponseEntity.ok("Room added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoom(@RequestBody RoomRequest roomRequest, @PathVariable Long id) {
        roomService.updateRoom(roomRequest, id);
        return ResponseEntity.ok("Room updated successfully");
    }
    @GetMapping("/filter")
    public ResponseEntity<Page<RoomResponse>> filterRooms(
            @RequestParam(required = false) RoomType type,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomResponse> rooms = roomService.filterRoomsByTypeAndStatus(type, status, pageable);
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }
}
