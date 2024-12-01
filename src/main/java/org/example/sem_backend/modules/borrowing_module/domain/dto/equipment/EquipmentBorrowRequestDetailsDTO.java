package org.example.sem_backend.modules.borrowing_module.domain.dto.equipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentBorrowRequestDetailsDTO {
    private Long requestId;
    private List<EquipmentBorrowRequestDetailDTO> details;
}
