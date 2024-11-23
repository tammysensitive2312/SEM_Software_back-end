package org.example.sem_backend.modules.equipment_module.repository;

import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findEquipmentByName(String equipmentName);

    @Query(value = "SELECT * FROM equipments e WHERE (:category IS NULL OR e.category = :category)",
            nativeQuery = true)
    Page<Equipment> findByCategory(@Param("category") String category, Pageable pageable);
}
