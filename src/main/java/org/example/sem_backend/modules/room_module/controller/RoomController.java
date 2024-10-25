package org.example.sem_backend.modules.room_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.dto.RoomDto;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Retrieve available rooms based on room type, date, and time period",
            description = "Fetch a list of available rooms of a specific type for a given date and period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available rooms",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String period) {
        List<RoomDto> availableRooms = roomService.findAvailableRooms(type, date, period);
        return ResponseEntity.ok(availableRooms);
    }

    @Operation(summary = "Search rooms based on capacity and room condition",
            description = "Fetch rooms based on capacity, comparison operator, and room condition.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<RoomDto>> searchRooms(
            @Parameter(description = "Capacity of the room")
            @RequestParam(required = false) Integer capacity,
            @Parameter(description = "Comparison operator for capacity (e.g., >, <, >=, <=, =)")
            @RequestParam(required = false) String comparisonOperator,
            @Parameter(description = "Room condition (e.g., available, in_use)")
            @RequestParam(required = false) String roomCondition) {

        List<RoomDto> rooms = roomService.findRooms(capacity, comparisonOperator, roomCondition);
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

