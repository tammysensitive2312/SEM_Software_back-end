package org.example.sem_backend.modules.equipment_module.repository;

import jakarta.persistence.QueryHint;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface represents a repository for managing {@link EquipmentDetail} entities.
 * It extends Spring Data JPA's {@link JpaRepository} interface, providing basic CRUD operations
 * and additional methods for querying and sorting equipment details.
 */
@Repository
public interface EquipmentDetailRepository extends JpaRepository<EquipmentDetail, Long> {
    Page<EquipmentDetail> findAllByOrderByRoomAsc(Pageable pageable);

    @Query("SELECT DISTINCT ed FROM equipment_details ed " +
            "JOIN fetch ed.equipment e " +
            "WHERE e.id = :equipmentId AND ed.status = 'USABLE'")
    @QueryHints({
            @QueryHint(name = "org.hibernate.timeout", value = "3"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name="org.hibernate.fetchSize", value = "50"),
            @QueryHint(name ="org.hibernate.cacheable", value = "true"),
            @QueryHint(name ="jakarta.persistence.cache.retrieveMode", value = "USE"),
            @QueryHint(name ="jakarta.persistence.cache.storeMode", value = "USE")
    })
    List<EquipmentDetail> findAvailableByEquipmentId(@Param("equipmentId") Long equipmentId, Pageable pageable);

    Page<EquipmentDetail> findByEquipmentId(Long equipmentId, Pageable pageable);

    @Query("SELECT ed FROM equipment_details ed " +
            "JOIN FETCH ed.equipment eq " +
            "JOIN FETCH ed.room r " +
            "WHERE r.uniqueId = :roomId")
    Page<EquipmentDetail> findByRoomId(Integer roomId, Pageable pageable);

    long countByEquipment(Equipment existingEquipment);


    @Query(value = "SELECT ed.* FROM sem_db.equipment_details ed " +
            "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(*) FROM sem_db.equipment_details ed " +
                    "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
                    "WHERE (:keyword IS NULL OR :keyword = '' " +
                    "OR LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            nativeQuery = true)
    List<EquipmentDetail> searchEquipmentDetail(@Param("keyword") String keyword);

    boolean existsBySerialNumber(String serialNumber);
}