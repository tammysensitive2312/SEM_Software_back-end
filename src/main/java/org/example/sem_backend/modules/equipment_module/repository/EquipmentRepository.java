package org.example.sem_backend.modules.equipment_module.repository;

import jakarta.persistence.QueryHint;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @QueryHints({
            @QueryHint(name="org.hibernate.readOnly", value = "true"),
            @QueryHint(name="org.hibernate.fetchSize", value = "50"),
            @QueryHint(name ="org.hibernate.cacheable", value = "true"),
            @QueryHint(name ="jakarta.persistence.cache.retrieveMode", value = "USE"),
            @QueryHint(name ="jakarta.persistence.cache.storeMode", value = "USE")
    })
    Optional<Equipment> findByEquipmentName(String equipmentName);

    @Query(value = "SELECT * FROM equipments e WHERE (:category IS NULL OR e.category = :category)",
            nativeQuery = true)
    Page<Equipment> findByCategory(@Param("category") String category, Pageable pageable);

    @Query(value = "SELECT * FROM equipments e WHERE " +
            "LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "LIMIT 5", nativeQuery = true)
    List<Equipment> searchEquipment(@Param("keyword") String keyword);

    boolean existsByCode(String code);
}
