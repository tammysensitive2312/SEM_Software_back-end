package org.example.sem_backend.modules.borrowing_module.repository;

import jakarta.persistence.QueryHint;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentBorrowRequestRepository extends JpaRepository<EquipmentBorrowRequest, Long> {
    @Query("SELECT COUNT(r) > 0 FROM EquipmentBorrowRequest r " +
            "WHERE r.user.id = :userId " +
            "AND r.status IN :statuses " +
            "AND r.expectedReturnDate < :date")
    @QueryHints({
            @QueryHint(name="org.hibernate.readOnly", value = "true"),
            @QueryHint(name="org.hibernate.fetchSize", value = "50"),
            @QueryHint(name ="org.hibernate.cacheable", value = "true"),
            @QueryHint(name ="jakarta.persistence.cache.retrieveMode", value = "USE"),
            @QueryHint(name ="jakarta.persistence.cache.storeMode", value = "USE")
    })
    boolean hasOverdueRequests(
            @Param("userId") Long userId,
            @Param("statuses") List<EquipmentBorrowRequest.Status> statuses,
            @Param("date") LocalDate date
    );

    Page<EquipmentBorrowRequest> findByUser_UsernameContainingIgnoreCase(String username, Pageable pageable);

    void deleteAllInBatch(Iterable<EquipmentBorrowRequest> entities);
}