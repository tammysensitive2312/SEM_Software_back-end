package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import lombok.Data;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;

import java.time.LocalDate;
import java.util.List;

@Data
public class EquipmentBorrowRequestFilterDTO {
    private Long userId;
    private List<EquipmentBorrowRequest.Status> statuses;
    private LocalDate expectedReturnDateBefore;
    private LocalDate expectedReturnDateAfter;
    private String username;
}
