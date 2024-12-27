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
