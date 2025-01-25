package org.example.sem_backend.common_module.service.schedule_service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.service.RoomScheduleProcessor;
import org.example.sem_backend.modules.notification_module.service.NotificationService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduledTaskManager {
    private final RoomScheduleProcessor roomScheduleProcessor;
    private final EquipmentBorrowRequestRepository equipmentBorrowRequestRepository;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final List<ScheduledTask> scheduledTask = new ArrayList<>();

    String cronExpression = "${task.cron.expression}";

    @PostConstruct
    public void scheduleTaskAtMidnight() {
        try {
            // Lập lịch quét database và xử lý lịch trình phòng
            taskScheduler.schedule(
                    roomScheduleProcessor::processRoomSchedules,
                    new CronTrigger(cronExpression)
            );

            // Lập lịch thông báo quá hạn
            scheduleOverdueNotificationTask(cronExpression);
        } catch (Exception e) {
            log.error("Failed to schedule tasks at midnight: {}", e.getMessage());
        }
    }


    private void scheduleOverdueNotificationTask(String cronExpression) {
        List<Long> userIds = equipmentBorrowRequestRepository.findUserIdsByExpectedReturnDateBeforeAndStatusIn(
                LocalDate.now(), EquipmentBorrowRequest.Status.BORROWED
        );

        taskScheduler.schedule(
                () -> notificationService.createAndSendNotification(userIds, "You have overdue equipment", true),
                new CronTrigger(cronExpression)
        );
    }


    public void registerTask(ScheduledTask task) {
        scheduledTask.add(task);
    }


    public void executeTask() {
        scheduledTask.forEach(ScheduledTask::execute);
    }

}
