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
    Optional<Equipment> findByEquipmentName(String equipmentName);

    @Query(value = "SELECT * FROM equipments e WHERE " +
            "(:category IS NULL OR :category = '' OR e.category = :category) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(*) FROM equipments e WHERE " +
                    "(:category IS NULL OR :category = '' OR e.category = :category) AND " +
                    "(:keyword IS NULL OR :keyword = '' OR " +
                    "LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            nativeQuery = true)
    Page<Equipment> searchEquipment(@Param("category") String category,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    boolean existsByCode(String code);
}
