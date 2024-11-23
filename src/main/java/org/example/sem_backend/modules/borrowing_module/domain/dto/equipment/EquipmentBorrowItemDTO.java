package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentBorrowItemDTO {
    @NotNull(message = "Tên thiết bị không được để trống")
    private String equipmentName;

    @Positive(message = "Số lượng phải lớn hơn 0")
    private int quantityBorrowed;

    private List<String> equipmentDetailCodes;

    private String conditionBeforeBorrow;
}
