package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {

    // Tìm các phòng theo loại và trạng thái có thể sử dụng, thời gian,sử dụng native query để tối ưu hiệu suất
    @Query(value = "SELECT r.* " +
            "FROM room r " +
            "WHERE r.type = :type " +
            "  AND r.status = 'available' " +
            "  AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM room_schedules rs " +
            "    WHERE rs.room_id = r.unique_id " +
            "      AND (:startTime < rs.end_time AND :endTime > rs.start_time) " +
            ")", nativeQuery = true)
    List<Room> findAvailableRooms(String type, LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "SELECT * FROM room WHERE (:type IS NULL OR type = :type) AND (:status IS NULL OR status = :status)", nativeQuery = true)
    Page<Room> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, Pageable pageable);

    boolean existsByDescription(@Param("description") String description);
}
