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
            "JOIN fetch ed.room r " +
            "WHERE r.type = 'WAREHOUSE' AND e.id = :equipmentId AND ed.status = 'USABLE'")
    @QueryHints({
            @QueryHint(name = "org.hibernate.timeout", value = "3"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name="org.hibernate.fetchSize", value = "50"),
            @QueryHint(name ="org.hibernate.cacheable", value = "true"),
            @QueryHint(name ="jakarta.persistence.cache.retrieveMode", value = "USE"),
            @QueryHint(name ="jakarta.persistence.cache.storeMode", value = "USE")
    })
    List<EquipmentDetail> findAvailableByEquipmentId(@Param("equipmentId") Long equipmentId, Pageable pageable);

    @Query("SELECT ed FROM equipment_details ed " +
            "JOIN FETCH ed.equipment eq " +
            "JOIN FETCH ed.room r " +
            "WHERE r.uniqueId = :roomId")
    Page<EquipmentDetail> findByRoomId(Integer roomId, Pageable pageable);

    long countByEquipment(Equipment existingEquipment);

//    Tìm kiếm thiết bị theo tên thiết bị hoặc số sê-ri trong toàn trường.
//    Nếu không truyền keyword thì sẽ trả ve tất cả
@Query(value = "SELECT ed.* FROM sem_db.equipment_details ed " +
        "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
        "WHERE (:keyword IS NULL OR :keyword = '' " +
        "OR LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
        "OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
        "AND ed.status = 'USABLE'",
        countQuery = "SELECT COUNT(*) FROM sem_db.equipment_details ed " +
                "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
                "WHERE (:keyword IS NULL OR :keyword = '' " +
                "OR LOWER(e.equipment_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                "AND ed.status = 'USABLE'",
        nativeQuery = true)
Page<EquipmentDetail> searchEquipmentDetail(@Param("keyword") String keyword, Pageable pageable);



    //    Lấy chi tiết thiết bị theo id của thiết bị và từ khóa tìm kiếm.
@Query(value = "SELECT ed.* FROM sem_db.equipment_details ed " +
        "JOIN sem_db.equipments e ON ed.equipment_id = e.id " +
        "WHERE ed.equipment_id = :equipmentId " +
        "AND (:keyword IS NULL OR :keyword = '' OR " +
        "LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
        "AND (:status IS NULL OR :status = '' OR ed.status = :status)",
        countQuery = "SELECT COUNT(*) FROM sem_db.equipment_details ed " +
                "WHERE ed.equipment_id = :equipmentId " +
                "AND (:keyword IS NULL OR :keyword = '' OR LOWER(ed.serial_number) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                "AND (:status IS NULL OR :status = '' OR ed.status = :status)",
        nativeQuery = true)
Page<EquipmentDetail> getEquipmentDetailByEquipmentId(@Param("equipmentId") Long equipmentId,
                                            @Param("keyword") String keyword,
                                            @Param("status") String status,
                                            Pageable pageable);


    boolean existsBySerialNumber(String serialNumber);

    @Query(value = "SELECT COUNT(ed.id) FROM equipment_details ed " +
            "JOIN rooms r ON ed.room_id = r.unique_id " +
            "WHERE ed.equipment_id = :equipmentId " +
            "AND ed.status = 'USABLE' AND r.type = 'WAREHOUSE'",
            nativeQuery = true)
    int countUsableEquipmentInWarehouseByEquipmentId(@Param("equipmentId") Long equipmentId);

}