package org.example.sem_backend.modules.equipment_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.enums.Category;
import org.example.sem_backend.modules.equipment_module.service.EquipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "Add new equipment",
            description = "Add a new equipment item to the system with specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> addEquipment(@Valid @RequestBody EquipmentRequest equipmentRequest) {
        equipmentService.addEquipment(equipmentRequest);
        return ResponseEntity.ok("Equipment added successfully");
    }

    @Operation(summary = "Update equipment",
            description = "Update an existing equipment item with new details.")
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

    @Operation(summary = "Search equipment",
            description = "Search for equipment items by keyword in name or code or category.")
    @GetMapping("/search")
    public ResponseEntity<Page<EquipmentResponse>> searchEquipment(@RequestParam(required = false) String category,
                                                                   @RequestParam(required = false) String keyword,
                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "15") int size) {
        Page<EquipmentResponse> equipments = equipmentService.searchEquipments(category, keyword, page, size);
        return ResponseEntity.ok(equipments);
    }

}
