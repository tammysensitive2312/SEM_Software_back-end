package org.example.sem_backend.modules.equipment_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
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
public class EquipmentDetailController {
    private final EquipmentDetailService equipmentDetailService;


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

    @Operation(summary = "Update equipment detail location",
            description = "Update the room of an equipment detail item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment detail location updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/location")
    public ResponseEntity<String> updateEquipmentDetailLocation(@RequestParam Long equipmentDetailId, @RequestParam Integer roomId) {
        equipmentDetailService.updateEquipmentDetailLocation(equipmentDetailId, roomId);
        return ResponseEntity.ok("Equipment detail location updated successfully");
    }

    @Operation(summary = "Get equipment details by equipment ID",
            description = "Get a list of equipment detail items by equipment ID.")
    @GetMapping("/equipment")
    public Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(@RequestParam Long equipmentId,
                                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                                          @RequestParam(value = "size", defaultValue = "15") int size) {
        return equipmentDetailService.getEquipmentDetailsByEquipmentId(equipmentId, page, size);
    }

    @Operation(summary = "Get equipment details by room ID",
            description = "Get a list of equipment detail items by room ID.")
    @GetMapping("/room")
    public Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(@RequestParam Integer roomId,
                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentDetailService.getEquipmentDetailsByRoomId(roomId, pageable);
    }

    @Operation(summary = "Search equipment detail",
            description = "Search for equipment detail items by keyword in name or se-ri.")
    @GetMapping("/search")
    public ResponseEntity<List<EquipmentDetailResponse>> searchEquipmentDetail(@RequestParam String keyword) {
        return ResponseEntity.ok(equipmentDetailService.searchEquipmentDetail(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEquipmentDetail(@PathVariable Long id) {
        equipmentDetailService.deleteEquipmentDetail(id);
        return ResponseEntity.ok("Equipment detail deleted successfully");
    }
}
