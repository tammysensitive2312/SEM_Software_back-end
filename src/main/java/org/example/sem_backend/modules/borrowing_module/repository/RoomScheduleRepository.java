package org.example.sem_backend.modules.borrowing_module.repository;

import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, Long> {

    /**
     * Tìm kiếm các lịch đặt phòng (RoomSchedule) dựa trên Room ID và khoảng thời gian yêu cầu.
     *
     * <p>
     * Phương thức này được sử dụng để kiểm tra lịch trình của một phòng cụ thể xem có bị trùng lặp với
     * khoảng thời gian yêu cầu (từ `startTime` đến `endTime`) hay không. Phương thức sẽ trả về danh sách
     * các lịch đặt phòng trùng lặp nếu tìm thấy.
     * </p>
     *
     * @return List<RoomSchedule> Danh sách các lịch đặt phòng trùng lặp nếu có. Trả về danh sách rỗng nếu không có lịch nào trùng.
     *
     * @apiNote Phương thức này hữu ích trong việc xác thực yêu cầu mượn phòng, đảm bảo rằng khoảng thời gian
     *          yêu cầu không xung đột với các lịch đặt phòng hiện có của cùng một phòng.
     */
    List<RoomSchedule> findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}