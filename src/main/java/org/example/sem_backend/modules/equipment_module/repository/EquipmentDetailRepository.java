package org.example.sem_backend.modules.equipment_module.repository;

import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This interface represents a repository for managing {@link EquipmentDetail} entities.
 * It extends Spring Data JPA's {@link JpaRepository} interface, providing basic CRUD operations
 * and additional methods for querying and sorting equipment details.
 */
@Repository
public interface EquipmentDetailRepository extends JpaRepository<EquipmentDetail, Long> {
    Page<EquipmentDetail> findAllByOrderByRoomAsc(Pageable pageable);

    boolean existsByCode(String code);

    Page<EquipmentDetail> findByEquipmentId(Long equipmentId, Pageable pageable);

    @Query("SELECT ed FROM EquipmentDetail ed " +
            "JOIN FETCH ed.equipment eq " +
            "JOIN FETCH ed.room r " +
            "WHERE r.uniqueId = :roomId")
    Page<EquipmentDetail> findByRoom_UniqueId(Long roomId, Pageable pageable);
}