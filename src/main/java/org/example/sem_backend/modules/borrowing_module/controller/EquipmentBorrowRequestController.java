package org.example.sem_backend.modules.borrowing_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDetailsDTO;
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

    @PutMapping("/{id}/approve")
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
    public ResponseEntity<?> approveBorrowRequest(@PathVariable Long id) {
        requestService.approveRequest(id);
        return ResponseEntity.ok("Borrow Request approved successfully");
    }

    @Operation(
            summary = "Lấy danh sách tất cả các đơn mượn thiết bị",
            description = "Trả về danh sách các đơn mượn thiết bị. Có thể lọc theo tên người dùng nếu truyền tham số `filter`."
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
            summary = "Lấy chi tiết của một đơn mượn thiết bị",
            description = "Trả về chi tiết thông tin của một đơn mượn thiết bị cụ thể dựa trên `id`."
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
            @Parameter(description = "ID của đơn mượn cần lấy chi tiết")
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
            summary = "Xóa danh sách các đơn mượn",
            description = "API này cho phép xóa nhiều đơn mượn cùng lúc dựa trên danh sách ID được truyền vào. Chỉ các đơn mượn chưa được duyệt mới có thể bị xóa.",
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
}
