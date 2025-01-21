package org.example.sem_backend.modules.borrowing_module.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.service.schedule_service.RoomStatusChangeTask;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomSchedule;
import org.example.sem_backend.modules.borrowing_module.repository.RoomScheduleRepository;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    public RoomScheduleProcessor(
            RoomScheduleRepository roomScheduleRepository,
            @Qualifier("primaryTaskScheduler") TaskScheduler scheduler,
            RoomService roomService) {
        this.roomScheduleRepository = roomScheduleRepository;
        this.scheduler = scheduler;
        this.roomService = roomService;
    }

    /**
     * Lập lịch định kỳ để xử lý thay đổi trạng thái phòng vào 12am mỗi ngày.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyRoomStatusUpdatesAtMidnight() {
        scheduler.schedule(
                this::processRoomSchedules,
                triggerContext -> {
                    LocalDateTime nextExecution = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
                    return nextExecution.atZone(java.time.ZoneId.systemDefault()).toInstant();
                }
        );
    }

    /**
     * Quét và lập lịch thay đổi trạng thái phòng dựa trên RoomSchedule.
     */
    public void processRoomSchedules() {
        List<RoomSchedule> schedules = roomScheduleRepository.findAll();
        Map<Long, List<RoomSchedule>> groupedSchedules = groupSchedulesByRoomId(schedules);

        groupedSchedules.forEach(this::processSchedulesForRoom);
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
        mutableSchedules.sort(

                Comparator.comparing(RoomSchedule::getStartTime));

        for (int i = 0; i < schedules.size(); i++) {
            RoomSchedule currentSchedule = schedules.get(i);
            LocalDateTime endTime = currentSchedule.getEndTime();

            // Lập lịch trạng thái "IN_USE"
            scheduler.schedule(
                    new RoomStatusChangeTask(currentSchedule.getStartTime(), log) {{
                        setRoomId(roomId);
                        setNewStatus(RoomStatus.IN_USE);
                        setRoomService(roomService);
                    }},
                    currentSchedule.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant()
            );

            // Xử lý trạng thái "AVAILABLE"
            if (i + 1 < schedules.size()) {
                RoomSchedule nextSchedule = schedules.get(i + 1);
                long gapMinutes = java.time.Duration.between(endTime, nextSchedule.getStartTime()).toMinutes();

                if (gapMinutes > 15) {
                    scheduler.schedule(
                            new RoomStatusChangeTask(endTime, log) {{
                                setRoomId(roomId);
                                setNewStatus(RoomStatus.AVAILABLE);
                                setRoomService(roomService);
                            }},
                            endTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
                    );
                }
            } else {
                scheduler.schedule(
                        new RoomStatusChangeTask(endTime, log) {{
                            setRoomId(roomId);
                            setNewStatus(RoomStatus.AVAILABLE);
                            setRoomService(roomService);
                        }},
                        endTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
                );
            }
        }
    }
}
