package org.example.sem_backend.modules.borrowing_module.repository;

import org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomBorrowRequestRepository extends JpaRepository<RoomBorrowRequest, Long> {
    @Query("""
    SELECT new org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO(
        r.uniqueID, rm.roomName, u.username, u.email, rs.startTime, rs.endTime, r.comment
    )
    FROM RoomBorrowRequest r
    LEFT JOIN r.user u
    LEFT JOIN r.room rm
    LEFT JOIN RoomSchedule rs ON rs.room.uniqueId = r.room.uniqueId
    WHERE (:userId IS NULL OR u.id = :userId)
      AND (:email IS NULL OR u.email LIKE %:email%)
      AND (:startTime IS NULL OR (rs.startTime >= :startTime AND rs.endTime <= :endTime))
    """)
    Page<GetRoomRequestDTO> findRequestsWithSchedules(
            @Param("userId") Long userId,
            @Param("email") String email,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

        /**
     * Retrieves a list of user IDs who have room bookings after a specified time for a given room.
     * This method uses a native SQL query to fetch the distinct user IDs from room borrow requests
     * that have associated room schedules starting after the provided current time.
     *
     * @param currentTime The date and time after which to check for bookings.
     * @param roomId The unique identifier of the room for which to check bookings.
     * @return A List of Long values representing the user IDs with future bookings for the specified room.
     */
    @Query(value = """
    SELECT DISTINCT r.user_id
    FROM room_borrow_requests r
    JOIN room_schedules rs ON rs.room_id = r.room_id
    WHERE rs.start_time > :currentTime
    AND rs.user = (
        SELECT u.email
        FROM users u
        WHERE u.id = r.user_id
    )
    AND rs.room_id = :roomId
    """, nativeQuery = true)
    List<Long> findUserIdsWithBookingsAfter(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("roomId") Long roomId
    );
}