package org.example.sem_backend.modules.room_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.dto.AvailableRoomDto;
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
                            schema = @Schema(implementation = AvailableRoomDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/available")
    public ResponseEntity<List<AvailableRoomDto>> getAvailableRooms(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String period) {
        try {
            List<AvailableRoomDto> availableRooms = roomService.findAvailableRooms(type, date, period);
            return ResponseEntity.ok(availableRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
