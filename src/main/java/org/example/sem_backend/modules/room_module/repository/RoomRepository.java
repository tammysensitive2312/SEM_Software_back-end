package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query(value = "SELECT * FROM room WHERE (:type IS NULL OR type = :type) AND (:status IS NULL OR status = :status)", nativeQuery = true)
    Page<Room> findByTypeAndStatus(@Param("type") String type, @Param("status") String status, Pageable pageable);

//    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM room WHERE room_name = :roomName", nativeQuery = true)
    boolean existsRoomByRoomName(String roomName);

}
