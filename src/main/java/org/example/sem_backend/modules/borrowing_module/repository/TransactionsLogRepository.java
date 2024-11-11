package org.example.sem_backend.modules.borrowing_module.repository;

import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsLogRepository extends JpaRepository<TransactionsLog, Long> {
}