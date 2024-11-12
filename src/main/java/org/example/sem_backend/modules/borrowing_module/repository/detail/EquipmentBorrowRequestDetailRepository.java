package org.example.sem_backend.modules.borrowing_module.repository.detail;

import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentBorrowRequestDetailRepository extends JpaRepository<EquipmentBorrowRequestDetail, Long> {
}