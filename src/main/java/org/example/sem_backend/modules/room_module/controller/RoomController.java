package org.example.sem_backend.modules.room_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private final IRoomService roomService;

    @Operation(summary = "Create a new room",
            description = "Create a new room with the given details")
    @PostMapping
    public ResponseEntity<String> addRoom(@Valid @RequestBody RoomRequest roomRequest) {
        roomService.addRoom(roomRequest);
        return ResponseEntity.ok("Room added successfully");
    }

    @Operation(summary = "Update a room",
            description = "Update an existing room with new details")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateRoom(@Valid @RequestBody RoomRequest roomRequest, @PathVariable Integer id) {
        roomService.updateRoom(roomRequest, id);
        return ResponseEntity.ok("Room updated successfully");
    }

    @Operation(summary = "Get rooms by type or status",
            description = "Retrieve a list of rooms matching the given type or status")
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

    @GetMapping("/search")
    @Operation(summary = "Search rooms by keyword", description = "Retrieve a list of rooms matching the given keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms"),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoomResponse>> searchRoom(
            @Parameter(description = "Keyword to search for") @RequestParam String keyword) {
        List<RoomResponse> rooms = roomService.searchRoom(keyword);
        return ResponseEntity.ok(rooms);
    }

}
