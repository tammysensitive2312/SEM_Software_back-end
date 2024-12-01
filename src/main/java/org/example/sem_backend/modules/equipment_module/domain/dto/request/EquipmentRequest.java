package org.example.sem_backend.modules.equipment_module.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.sem_backend.modules.equipment_module.enums.Category;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequest {
    @NotBlank(message = "Equipment name is required")
    private String equipmentName;

    private Category category;

    @NotBlank(message = "Code is required")
    private String code;
}
