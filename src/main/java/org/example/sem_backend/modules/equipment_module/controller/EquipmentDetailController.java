package org.example.sem_backend.modules.equipment_module.controller;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.service.EquipmentDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/equipment-detail")
public class EquipmentDetailController {
    private final EquipmentDetailService equipmentDetailService;
    @GetMapping("/equipment")
    public Page<GetEquipmentResponseDto> getEquipmentDetailsByEquipmentId(@RequestParam Long equipmentId,
                                                                          @RequestParam int page,
                                                                          @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentDetailService.getEquipmentDetailsByEquipmentId(equipmentId, pageable);
    }

    @GetMapping("/room")
    public Page<GetEquipmentResponseDto> getEquipmentDetailsByRoomId(@RequestParam Long roomId,
                                                                     @RequestParam int page,
                                                                     @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return equipmentDetailService.getEquipmentDetailsByRoomId(roomId, pageable);
    }
}
