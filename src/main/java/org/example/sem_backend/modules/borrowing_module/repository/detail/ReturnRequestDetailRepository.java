package org.example.sem_backend.modules.borrowing_module.repository.detail;

import org.example.sem_backend.modules.borrowing_module.domain.entity.ReturnRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestDetailRepository extends JpaRepository<ReturnRequestDetail, Long> {
}