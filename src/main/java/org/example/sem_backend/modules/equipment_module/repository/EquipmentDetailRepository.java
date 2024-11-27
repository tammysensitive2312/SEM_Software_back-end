package org.example.sem_backend.modules.equipment_module.repository;

import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT ed FROM equipment_details ed WHERE ed.serialNumber IN :serialNumbers")
    List<EquipmentDetail> findByCodes(@Param("serialNumbers") List<String> serialNumbers);

    @Query("SELECT ed FROM equipment_details ed WHERE ed.equipment.id = :equipmentId AND ed.status = 'USABLE'")
    List<EquipmentDetail> findAvailableByEquipmentId(@Param("equipmentId") Long equipmentId, Pageable pageable);

    Page<EquipmentDetail> findByEquipmentId(Long equipmentId, Pageable pageable);


    @Query("SELECT ed FROM equipment_details ed " +
            "JOIN FETCH ed.equipment eq " +
            "JOIN FETCH ed.room r " +
            "WHERE r.uniqueId = :roomId")
    Page<EquipmentDetail> findByRoomId(Integer roomId, Pageable pageable);

    long countByEquipment(Equipment existingEquipment);

//    @Query("SELECT e FROM Room e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) LIMIT 5")
//    List<Room> searchRoom(@Param("keyword") String keyword);

    @Query(value = "SELECT ed.* FROM sem_db.equipment_details ed " +
            "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
            "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY ed.id " +
            "LIMIT 5", nativeQuery = true)
    List<EquipmentDetail> searchEquipmentDetail(@Param("keyword") String keyword);

}