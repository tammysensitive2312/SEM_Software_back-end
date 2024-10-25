package org.example.sem_backend.modules.equipment_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

//    @Operation(summary = "Retrieve all equipment sorted by room number",
//            description = "Fetch a paginated list of equipment sorted by the room in which they are located.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "api thực hiện thành công", content = @Content(mediaType = "application/json")),
//            @ApiResponse(responseCode = "400", description = "sai tham số", content = @Content),
//            @ApiResponse(responseCode = "500", description = "có lỗi khi server xử lý", content = @Content)
//    })
//    @GetMapping("/sorted-by-room")
//    public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(
//            @Parameter(description = "Pagination information such as page number and size") Pageable pageable) {
//        return equipmentService.getAllEquipmentSortedByRoom(pageable);
//    }
}
