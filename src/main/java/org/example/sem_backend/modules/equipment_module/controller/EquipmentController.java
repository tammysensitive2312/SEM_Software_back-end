package org.example.sem_backend.modules.equipment_module.controller;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.service.EquipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/sorted-by-room")
    public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(Pageable pageable) {
        return equipmentService.getAllEquipmentSortedByRoom(pageable);
    }
}
