package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EquipmentBorrowRequestDTO {
    private Long uniqueID;

    @Schema(description = "UserID sẽ được loại bỏ trong tương lai khi triển khai cơ chế " +
            "xác thực với JWT (truyền userId vào payload của token)")
    private Long userId;

    private String comment;

    @NotNull(message = "ExpectedReturnDate must not be null")
    private LocalDate expectedReturnDate;

    private List<EquipmentBorrowItemDTO> equipmentItems;
}