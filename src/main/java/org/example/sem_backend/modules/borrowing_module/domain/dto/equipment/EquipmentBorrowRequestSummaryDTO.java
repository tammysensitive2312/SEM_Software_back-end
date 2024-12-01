package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EquipmentBorrowRequestSummaryDTO extends EquipmentBorrowRequestDTO{
    private String userName;
    private EquipmentBorrowRequest.Status status;
    private LocalDate createdAt;
}
