package org.example.sem_backend.modules.borrowing_module.repository;

import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TransactionsLogRepository extends JpaRepository<TransactionsLog, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM TransactionsLog t WHERE t.equipmentRequest.uniqueID IN :requestIds")
    void deleteByEquipmentRequestIds(@Param("requestIds") List<Long> requestIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM TransactionsLog t WHERE t.roomRequest.uniqueID IN :requestIds")
    void deleteByRoomRequestIds(@Param("requestIds") List<Long> requestIds);
}