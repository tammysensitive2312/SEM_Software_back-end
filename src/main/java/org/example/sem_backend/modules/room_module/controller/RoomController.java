package org.example.sem_backend.modules.room_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.dto.RoomDto;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.enums.RoomType;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room", description = "Update room details using room ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> updateRoom(
            @RequestBody RoomDto roomRequest,
            @Parameter(description = "ID of the room to update", required = true) @PathVariable Long id) {
        roomService.updateRoom(roomRequest, id);
        return ResponseEntity.ok("Room updated successfully");
    }

    @PostMapping
    @Operation(summary = "Add a new room", description = "Add a new room with given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid room data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> addRoom(
            @RequestBody @Valid RoomDto roomRequest) {
        roomService.addRoom(roomRequest);
        return ResponseEntity.ok("Room added successfully");
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter rooms by type and status", description = "Retrieve a paginated list of rooms filtered by type and/or status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<RoomDto>> filterRooms(
            @Parameter(description = "Type of room to filter") @RequestParam(required = false) RoomType type,
            @Parameter(description = "Status of room to filter") @RequestParam(required = false) RoomStatus status,
            @Parameter(description = "Page number for pagination", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for pagination", example = "12") @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomDto> rooms = roomService.filterRoomsByTypeAndStatus(type, status, pageable);
        return ResponseEntity.ok(rooms);
    }
}
