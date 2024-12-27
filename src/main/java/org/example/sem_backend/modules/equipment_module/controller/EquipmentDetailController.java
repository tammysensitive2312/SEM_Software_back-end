package org.example.sem_backend.modules.equipment_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentDetailLocationRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.service.EquipmentDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/equipment-detail")
@Tag(name = "Equipment Detail Controller", description = "APIs for handling manage EquipmentDetail")
public class EquipmentDetailController {
    private final EquipmentDetailService equipmentDetailService;

    @Operation(summary = "Retrieve all equipment sorted by room number",
            description = "Fetch a paginated list of equipment sorted by the room in which they are located.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved equipment list sorted by room",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/sorted-by-room")
    public Page<EquipmentDetailResponse> getAllEquipmentSortedByRoom(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentDetailService.getAllEquipmentSortedByRoom(pageable);
    }

    @Operation(summary = "Add new equipment detail",
            description = "Add a new equipment detail item to the system with specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment detail added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<String> addEquipmentDetail(@RequestBody EquipmentDetailRequest request) {
        equipmentDetailService.addEquipmentDetail(request);
        return ResponseEntity.ok("Equipment detail added successfully");
    }

    @Operation(summary = "Add a list of new equipment detail",
            description = "Add a list of new equipment detail item to the system with specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment detail added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add-list")
    public ResponseEntity<String> addListEquipmentDetail(@RequestBody List<EquipmentDetailRequest> requests) {
        equipmentDetailService.addListEquipmentDetail(requests);
        return ResponseEntity.ok("List Equipment detail added successfully");
    }

    @Operation(summary = "Update equipment detail",
              description = "Update an existing equipment detail item with new details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment detail updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateEquipmentDetail(@PathVariable Long id, @Valid @RequestBody EquipmentDetailRequest request) {
        equipmentDetailService.updateEquipmentDetail(id, request);
        return ResponseEntity.ok("Equipment detail updated successfully");
    }


    @Operation(summary = "Update equipment detail location",
            description = "Update the room of an equipment detail item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment detail location updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/location/room/{roomId}")
    public ResponseEntity<String> updateEquipmentDetailLocation(@RequestBody UpdateEquipmentDetailLocationRequest requests, @PathVariable Long roomId) {
        equipmentDetailService.updateEquipmentDetailLocation(requests.getEquipmentDetailIds(), roomId);
        return ResponseEntity.ok("Equipment detail location updated successfully");
    }

    @Operation(summary = "Get equipment details by equipment ID",
            description = "Get a list of equipment detail items by equipment ID.")
    @GetMapping("/equipment/{equipmentId}")
    public Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(@PathVariable Long equipmentId,
                                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                                          @RequestParam(value = "size", defaultValue = "15") int size) {
        return equipmentDetailService.getEquipmentDetailsByEquipmentId(equipmentId, page, size);
    }

    @Operation(summary = "Get equipment details by room ID",
            description = "Get a list of equipment detail items by room ID.")
    @GetMapping("/room/{roomId}")
    public Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(@PathVariable Integer roomId,
                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentDetailService.getEquipmentDetailsByRoomId(roomId, pageable);
    }

    @Operation(summary = "Search equipment detail",
            description = "Search for equipment detail items by keyword in name or se-ri.")
    @GetMapping("/search")
    public ResponseEntity<List<EquipmentDetailResponse>> searchEquipmentDetail(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(equipmentDetailService.searchEquipmentDetail(keyword));
    }

    @Operation(summary = "Delete equipment detail",
            description = "Delete an equipment detail item by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEquipmentDetail(@PathVariable Long id) {
        equipmentDetailService.deleteEquipmentDetail(id);
        return ResponseEntity.ok("Equipment detail deleted successfully");
    }
}
