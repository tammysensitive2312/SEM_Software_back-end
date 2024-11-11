package org.example.sem_backend.modules.borrowing_module.controller;

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
public class BorrowRequestController {

    private final RoomBorrowRequestService service;

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createBookingRequest(
            @RequestBody @Valid RoomBorrowRequestDTO requestDto
    ) {
        service.processRequest(requestDto);
        return ResponseEntity.accepted().build();
    }
}
