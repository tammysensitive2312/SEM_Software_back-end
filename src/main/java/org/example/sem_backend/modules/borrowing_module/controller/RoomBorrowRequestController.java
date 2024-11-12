package org.example.sem_backend.modules.borrowing_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.service.Impl.RoomBorrowRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrow/room")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room Borrow Requests Controller", description = "APIs for handling borrow requests")
public class RoomBorrowRequestController {

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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createBookingRequest(
            @RequestBody @Valid RoomBorrowRequestDTO requestDto
    ) {
        service.processRequest(requestDto);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/partialUpdate")
    @Operation(
            summary = "Partially update a room booking request",
            description = "Update specific fields of an existing room booking request if it was created within the last 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the booking request"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "409", description = "Update not allowed - overdue correction time")
    })
    public ResponseEntity<RoomBorrowRequest> updateBookingRequest(
            @RequestBody @Valid RoomBorrowRequestDTO requestDto
    ) {
        RoomBorrowRequest updatedRequest = service.updateRequest(requestDto);
        return ResponseEntity.ok().body(updatedRequest);
    }

}

