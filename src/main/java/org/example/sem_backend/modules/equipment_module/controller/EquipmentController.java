package org.example.sem_backend.modules.equipment_module.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.entity.Category;
import org.example.sem_backend.modules.equipment_module.service.EquipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<String> addEquipment(@Valid @RequestBody EquipmentRequest equipmentRequest) {
        equipmentService.addEquipment(equipmentRequest);
        return ResponseEntity.ok("Equipment added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEquipment(@Valid @RequestBody UpdateEquipmentRequest request, @PathVariable Long id) {
        equipmentService.updateEquipment(id, request);
        return ResponseEntity.ok("Equipment updated successfully");
    }

    @GetMapping
    public ResponseEntity<Page<EquipmentResponse>> getEquipmentsByCategory(
            @RequestParam(required = false) Category category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EquipmentResponse> equipments = equipmentService.getEquipmentsByCategory(category, pageable);
        return ResponseEntity.ok(equipments);
    }

}
