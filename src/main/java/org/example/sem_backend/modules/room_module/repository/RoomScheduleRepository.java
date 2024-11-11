package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, Long> {
    List<RoomSchedule> findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}