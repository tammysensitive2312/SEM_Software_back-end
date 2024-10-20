package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tìm các phòng theo loại và trạng thái có thể sử dụng
    @Query("SELECT r FROM Room r " +
            "LEFT JOIN FETCH r.roomSchedules s " +
            "WHERE r.type = :type AND r.roomCondition = 'available' " +
            "AND NOT EXISTS (SELECT 1 FROM RoomSchedule rs " +
            "WHERE rs.room.uniqueId = r.uniqueId " +
            "AND ((:startTime BETWEEN rs.startTime AND rs.endTime) " +
            "OR (:endTime BETWEEN rs.startTime AND rs.endTime)))")
    List<Room> findAvailableRooms(String type, String startTime, String endTime);
}
