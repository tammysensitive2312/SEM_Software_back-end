package org.example.sem_backend.modules.borrowing_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.borrowing_module.domain.dto.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.service.Impl.RoomBorrowRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrow")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Borrow Requests", description = "APIs for handling room borrow requests")
public class BorrowRequestController {

    private final RoomBorrowRequestService service;

    @Operation(
            summary = "Create a new room booking request",
            description = "Creates a new room booking request if the room is available and within the allowed booking window"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Booking request accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - Room is already booked or booking window exceeded", content = @Content),
            @ApiResponse(responseCode = "404", description = "Room or User not found", content = @Content)
    })
    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createBookingRequest(
            @RequestBody @Valid RoomBorrowRequestDTO requestDto
    ) {
        service.processRequest(requestDto);
        return ResponseEntity.accepted().build();
    }
}

