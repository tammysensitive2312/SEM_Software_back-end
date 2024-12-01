package org.example.sem_backend.modules.equipment_module.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDetailResponse {
    private Long id;
    private String equipmentName;
    private String roomName;
    private String category;
    private String serialNumber;
    private String description;
    private String status;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;
}
