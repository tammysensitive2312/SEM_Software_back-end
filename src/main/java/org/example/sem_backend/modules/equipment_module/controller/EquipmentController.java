package org.example.sem_backend.modules.equipment_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.CreateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.enums.Category;
import org.example.sem_backend.modules.equipment_module.service.EquipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "Retrieve all equipment sorted by room number",
            description = "Fetch a paginated list of equipment sorted by the room in which they are located.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved equipment list sorted by room",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetEquipmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/sorted-by-room")
    public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentService.getAllEquipmentSortedByRoom(pageable);
    }

    @Operation(summary = "Add new equipment",
            description = "Add a new equipment item to the system with specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> addEquipment(
            @Valid @RequestBody CreateEquipmentRequest equipmentRequest) {
        equipmentService.addEquipment(equipmentRequest);
        return ResponseEntity.ok("Equipment added successfully");
    }

    @Operation(summary = "Update existing equipment",
            description = "Update details of an existing equipment item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Equipment not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEquipment(
            @Valid @RequestBody UpdateEquipmentRequest request,
            @Parameter(description = "ID of the equipment to be updated") @PathVariable Long id) {
        equipmentService.updateEquipment(id, request);
        return ResponseEntity.ok("Equipment updated successfully");
    }

    @Operation(summary = "Retrieve equipment by category",
            description = "Fetch a paginated list of equipment based on category filter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved equipment by category",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category or pagination parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<EquipmentResponse>> getEquipmentsByCategory(
            @Parameter(description = "Category of equipment to filter by, optional") @RequestParam(required = false) Category category,
            @Parameter(description = "Page number for pagination, default is 0") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Size of each page, default is 15") @RequestParam(value = "size", defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EquipmentResponse> equipments = equipmentService.getEquipmentsByCategory(category, pageable);
        return ResponseEntity.ok(equipments);
    }
}
