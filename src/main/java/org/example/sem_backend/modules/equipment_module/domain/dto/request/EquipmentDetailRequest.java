package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;

import java.time.LocalDate;

@Getter
@Setter
public class EquipmentDetailRequest {
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;

    @NotNull(message = "Status is required")
    private EquipmentDetailStatus status;

    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;

    @NotNull(message = "Equipment ID is required")
    private Long roomId;
}
