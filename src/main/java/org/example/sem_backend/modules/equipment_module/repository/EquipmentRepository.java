package org.example.sem_backend.modules.equipment_module.repository;

import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.hibernate.query.spi.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Equipment findByEquipmentName(String equipmentName);

    @Query(value = "SELECT * FROM equipment e WHERE (:category IS NULL OR e.category = :category)",
            nativeQuery = true)
    Page<Equipment> findByCategory(@Param("category") String category, Pageable pageable);

    @Query(value = "SELECT * FROM equipment e WHERE " +
            "LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "LIMIT 5", nativeQuery = true)
    List<Equipment> searchEquipment(@Param("keyword") String keyword);

}
