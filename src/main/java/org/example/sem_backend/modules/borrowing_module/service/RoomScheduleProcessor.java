package org.example.sem_backend.modules.borrowing_module.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.service.schedule_service.RoomStatusChangeTask;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomSchedule;
import org.example.sem_backend.modules.borrowing_module.repository.RoomScheduleRepository;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomScheduleProcessor {

    private final RoomScheduleRepository roomScheduleRepository;
    private final TaskScheduler scheduler;
    private final RoomService roomService;

    @Value("${schedule.task.min_gap}")
    private int minGapMinutes;

    public
    RoomScheduleProcessor(
            RoomScheduleRepository roomScheduleRepository,
            @Qualifier("primaryTaskScheduler") TaskScheduler scheduler,
            RoomService roomService) {
        this.roomScheduleRepository = roomScheduleRepository;
        this.scheduler = scheduler;
        this.roomService = roomService;
    }

    /**
     * Quét và lập lịch thay đổi trạng thái phòng dựa trên RoomSchedule.
     */
    public void processRoomSchedules() {
        try {
            List<RoomSchedule> schedules = roomScheduleRepository.findAll();
            Map<Long, List<RoomSchedule>> groupedSchedules = groupSchedulesByRoomId(schedules);

            groupedSchedules.forEach(this::processSchedulesForRoom);
        } catch (Exception e) {
            log.error("Failed to process room schedules: {}", e.getMessage());
        }
    }


    /**
     * Nhóm các RoomSchedule theo Room ID.
     *
     * @param schedules Danh sách các RoomSchedule.
     * @return Map với Room ID là key và danh sách RoomSchedule là value.
     */
    private Map<Long, List<RoomSchedule>> groupSchedulesByRoomId(List<RoomSchedule> schedules) {
        return schedules.stream().collect(Collectors.groupingBy(schedule -> schedule.getRoom().getUniqueId()));
    }

    /**
     * Xử lý lịch trình cho từng phòng.
     *
     * @param roomId    ID của phòng.
     * @param schedules Danh sách lịch trình của phòng.
     */
    public void processSchedulesForRoom(Long roomId, List<RoomSchedule> schedules) {
        List<RoomSchedule> mutableSchedules = new ArrayList<>(schedules);
        mutableSchedules.sort(Comparator.comparing(RoomSchedule::getStartTime));

        for (int i = 0; i < mutableSchedules.size(); i++) {
            RoomSchedule currentSchedule = mutableSchedules.get(i);
            LocalDateTime endTime = currentSchedule.getEndTime();

            // Lập lịch trạng thái "IN_USE"
            scheduler.schedule(
                    createRoomStatusChangeTask(roomId, RoomStatus.IN_USE, currentSchedule.getStartTime()),
                    currentSchedule.getStartTime().atZone(ZoneId.systemDefault()).toInstant()
            );

            // Lập lịch trạng thái "AVAILABLE"
            if (i + 1 < mutableSchedules.size()) {
                RoomSchedule nextSchedule = mutableSchedules.get(i + 1);
                if (shouldScheduleTask(endTime, nextSchedule.getStartTime(), minGapMinutes)) {
                    scheduleRoomAvailableTask(roomId, endTime);
                }
            } else {
                scheduleRoomAvailableTask(roomId, endTime);
            }
        }
    }


    private boolean shouldScheduleTask(LocalDateTime endTime, LocalDateTime nextStartTime, int minGapMinutes) {
        return java.time.Duration.between(endTime, nextStartTime).toMinutes() > minGapMinutes;
    }

    private RoomStatusChangeTask createRoomStatusChangeTask(Long roomId, RoomStatus status, LocalDateTime executionTime) {
        return new RoomStatusChangeTask(executionTime, log) {{
            setRoomId(roomId);
            setNewStatus(status);
            setRoomService(roomService);
        }};
    }

    private void scheduleRoomAvailableTask(Long roomId, LocalDateTime endTime) {
        scheduler.schedule(
                createRoomStatusChangeTask(roomId, RoomStatus.AVAILABLE, endTime),
                endTime.atZone(ZoneId.systemDefault()).toInstant()
        );
    }

}
