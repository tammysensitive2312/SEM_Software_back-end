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
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    // Tìm các phòng theo loại và trạng thái có thể sử dụng, thời gian,sử dụng native query để tối ưu hiệu suất
    @Query(value = "SELECT r.* " +
            "FROM rooms r " +
            "WHERE r.type = :type " +
            "  AND r.status = 'available' " +
            "  AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM room_schedules rs " +
            "    WHERE rs.room_id = r.unique_id " +
            "      AND (:startTime < rs.end_time AND :endTime > rs.start_time) " +
            ")", nativeQuery = true)
    List<Room> findAvailableRooms(String type, LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "SELECT * FROM rooms WHERE (:type IS NULL OR type = :type) AND (:status IS NULL OR status = :status)", nativeQuery = true)
    Page<Room> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, Pageable pageable);

    boolean existsByRoomName(String roomName);

    @Query(value = "SELECT * FROM rooms r WHERE LOWER(r.room_name) LIKE LOWER(CONCAT('%', :keyword, '%')) LIMIT 5", nativeQuery = true)
    List<Room> searchRoom(@Param("keyword") String keyword);

    @Query(value = """
    SELECT * FROM rooms r 
    WHERE (:type IS NULL OR r.type = :type) 
      AND (:status IS NULL OR r.status = :status) 
      AND (:keyword IS NULL OR LOWER(r.room_name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """,
            countQuery = """
    SELECT COUNT(*) FROM rooms r 
    WHERE (:type IS NULL OR r.type = :type) 
      AND (:status IS NULL OR r.status = :status) 
      AND (:keyword IS NULL OR LOWER(r.room_name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """,
            nativeQuery = true)
    Page<Room> findByTypeStatusAndKeyword(
            @Param("type") String type,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

}
