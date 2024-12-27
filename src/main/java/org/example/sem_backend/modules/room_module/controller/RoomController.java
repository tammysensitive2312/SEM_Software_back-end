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
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Retrieve available rooms based on room type, date, and time period",
            description = "Fetch a list of available rooms of a specific type for a given date and period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available rooms",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String period) {
        List<RoomResponse> availableRooms = roomService.findAvailableRooms(type, date, period);
        return ResponseEntity.ok(availableRooms);
    }

    @Operation(summary = "Search rooms based on capacity and room condition",
            description = "Fetch rooms based on capacity, comparison operator, and room condition.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved rooms",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content)
    })
    @GetMapping("/searchByCondition")
    public ResponseEntity<List<RoomResponse>> searchRoomsByCondition(
            @Parameter(description = "Capacity of the room")
            @RequestParam(required = false) Integer capacity,
            @Parameter(description = "Comparison operator for capacity (e.g., >, <, >=, <=, =)")
            @RequestParam(required = false) String comparisonOperator,
            @Parameter(description = "Room condition (e.g., available, in_use)")
            @RequestParam(required = false) String roomCondition) {

        List<RoomResponse> rooms = roomService.findRooms(capacity, comparisonOperator, roomCondition);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room", description = "Update room details using room ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<String> updateRoom(
            @RequestBody RoomRequest roomRequest,
            @Parameter(description = "ID of the room to update", required = true) @PathVariable Long id) {
        roomService.updateRoom(roomRequest, id);
        return ResponseEntity.ok("Room updated successfully");
    }

    @PostMapping
    @Operation(summary = "Add a new room", description = "Add a new room with given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid room data")
    })
    public ResponseEntity<String> addRoom(
            @RequestBody @Valid RoomRequest roomRequest) {
        roomService.addRoom(roomRequest);
        return ResponseEntity.ok("Room added successfully");
    }

    @Operation(summary = "Search rooms by keyword, type, status", description = "Retrieve a list of rooms matching the given keyword, type, and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms"),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<RoomResponse>> searchRoom(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomResponse> rooms = roomService.searchRoom(type, status, keyword, pageable);
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Delete a room using room ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<String> deleteRoom(
            @Parameter(description = "ID of the room to delete", required = true) @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }
}
