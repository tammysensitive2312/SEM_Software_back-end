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

@Repository
public interface RoomBorrowRequestRepository extends JpaRepository<RoomBorrowRequest, Long> {
    @Query("""
    SELECT new org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO(
        r.uniqueID, rm.roomName, u.username, rs.startTime, rs.endTime
    )
    FROM RoomBorrowRequest r
    LEFT JOIN r.user u
    LEFT JOIN r.room rm
    LEFT JOIN RoomSchedule rs ON rs.room.uniqueId = r.room.uniqueId
    WHERE (:userId IS NULL OR u.id = :userId)
      AND (:username IS NULL OR u.username LIKE %:username%)
      AND (:startTime IS NULL OR (rs.startTime >= :startTime AND rs.endTime <= :endTime))
    """)
    Page<GetRoomRequestDTO> findRequestsWithSchedules(
            @Param("userId") Long userId,
            @Param("username") String username,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
}