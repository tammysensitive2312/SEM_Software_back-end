package org.example.sem_backend.modules.borrowing_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDetailsDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestFilterDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestSummaryDTO;
import org.example.sem_backend.modules.borrowing_module.service.Impl.EquipmentBorrowRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrow/equipment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equipment Borrow Requests Controller", description = "APIs for handling borrow requests")
public class EquipmentBorrowRequestController {
    private final EquipmentBorrowRequestService requestService;

    @Operation(
            summary = "Create new equipment borrow request",
            description = "Creates a new request for borrowing equipment. Returns HTTP 202 if request is accepted for processing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Request accepted for processing"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data provided"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Resource conflict - e.g. equipment not available or user has overdue items"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<Void> createRequest(
            @RequestBody @Valid EquipmentBorrowRequestDTO requestDTO) {
        requestService.processRequest(requestDTO);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Approve a borrow request",
            description = "Approve a borrow request by its ID. This action will validate the availability of equipment and assign specific items to the request."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Borrow request approved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Borrow request not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict in approving the request"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request state"
            )
    })
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveBorrowRequest(@PathVariable Long id) {
        requestService.approveRequest(id);
        return ResponseEntity.ok("Borrow Request approved successfully");
    }

    @Operation(
            summary = "Retrieve all equipment borrow requests",
            description = "Returns a list of all equipment borrow requests. Can be filtered by user name."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách đơn mượn thiết bị trả về thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentBorrowRequestSummaryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Yêu cầu không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping("/list")
    public ResponseEntity<Page<EquipmentBorrowRequestSummaryDTO>> getAllRequests(
            @Parameter(description = "Tên người dùng để lọc các đơn mượn")
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable
    ) {
        Page<EquipmentBorrowRequestSummaryDTO> requests = requestService.getAllRequests(filter, pageable);
        return ResponseEntity.ok(requests);
    }

    @Operation(
            summary = "API to filter equipment borrow requests by many arguments",
            description = "Returns a list of equipment borrow requests that match the filter criteria. Can be filtered by user name, status, and date range."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Danh sách đơn mượn thiết bị trả về thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentBorrowRequestSummaryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Yêu cầu không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping("/filter")
    public ResponseEntity<Page<EquipmentBorrowRequestSummaryDTO>> filterEquipmentBorrowRequests(
            EquipmentBorrowRequestFilterDTO filterDTO,
            Pageable pageable
    ) {
        Page<EquipmentBorrowRequestSummaryDTO> requests = requestService.getFilteredRequests(filterDTO, pageable);
        return ResponseEntity.ok(requests);
    }

    @Operation(
            summary = "Retrieve detail of a borrow request",
            description = "Returns detailed information of a borrow request by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Chi tiết đơn mượn thiết bị trả về thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentBorrowRequestDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy đơn mượn với ID được cung cấp",
                    content = @Content
            )
    })
    @GetMapping("/list/{id}/details")
    public ResponseEntity<EquipmentBorrowRequestDetailsDTO> getRequestDetails(
            @Parameter(description = "equipment borrow request ID")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(requestService.getRequestDetails(id));
    }

    @PutMapping("/edit")
    @Operation(
            summary = "update a equipment borrow request",
            description = "Update an existing equipment borrow request if it was created within the last 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "409", description = "Update not allowed - overdue correction time")
    })
    public ResponseEntity<?> updateBookingRequest(
            @RequestBody @Valid EquipmentBorrowRequestDTO requestDto
    ) {
        requestService.updateRequest(requestDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Delete multiple borrow requests",
            description = "Delete multiple borrow requests by their IDs. Only requests with PENDING status can be deleted.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "400", description = "Danh sách ID không hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn mượn cho các ID được truyền vào"),
                    @ApiResponse(responseCode = "409", description = "Một hoặc nhiều đơn mượn đã được xử lý không thể xóa")
            }
    )
    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteRequests(@RequestBody List<Long> requestIds) {
        requestService.deleteRequestsByIds(requestIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Return borrowed equipment",
            description = "Update status of multiple borrow requests to RETURNED. Only requests with BORROWED status can be processed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipment returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No borrow requests found with provided IDs",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Some requests are not in BORROWED status",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @PatchMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    public void returnEquipment(
            @Parameter(
                    description = "List of borrow request IDs to be returned",
                    required = true
            )
            @RequestBody @NotEmpty(message = "Request IDs list cannot be empty")
            List<@NotNull(message = "Request ID cannot be null") Long> requestIds
    ) throws BadRequestException {
        requestService.returnEquipment(requestIds);
    }
}
