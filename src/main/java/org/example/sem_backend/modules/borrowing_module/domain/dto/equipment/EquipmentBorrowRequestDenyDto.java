package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentBorrowRequestDenyDto {
    private Long requestId;
    private String reason;
}
