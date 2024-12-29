package org.example.sem_backend.modules.borrowing_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.service.Impl.RoomBorrowRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<?> updateBookingRequest(
            @RequestBody @Valid RoomBorrowRequestDTO requestDto
    ) {
        service.updateRequest(requestDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Delete multiple requests by list ID",
            description = "API này đang được phát triển nhưng dự kiến không sử dụng",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "400", description = "Danh sách ID không hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn mượn cho các ID được truyền vào"),
                    @ApiResponse(responseCode = "409", description = "Một hoặc nhiều đơn mượn đã được xử lý không thể xóa")
            }
    )
    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteRequests(@RequestBody List<Long> requestIds) {
        service.deleteRequestsByIds(requestIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get user-specific room borrow requests",
            description = "Retrieve a paginated list of room borrow requests for a specific user. "
                    + "Optional filters include start time and end time."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of user room borrow requests",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/user-request")
    public ResponseEntity<Page<GetRoomRequestDTO>> getUserRequests(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        Page<GetRoomRequestDTO> requests = service.getUserRequests(userId, startDate, endDate, pageable);
        return ResponseEntity.ok(requests);
    }


    @Operation(
            summary = "Get room borrow requests for admin",
            description = "Retrieve a paginated list of room borrow requests for administrative purposes. "
                    + "Optional filters include username, start time, and end time."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of admin room borrow requests",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/admin-request")
    public ResponseEntity<Page<GetRoomRequestDTO>> getAdminRequests(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        Page<GetRoomRequestDTO> requests = service.getAdminRequests(email, startDate, endDate, pageable);
        return ResponseEntity.ok(requests);
    }
}

