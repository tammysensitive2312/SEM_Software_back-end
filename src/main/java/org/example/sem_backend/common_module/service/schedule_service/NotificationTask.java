package org.example.sem_backend.common_module.service.schedule_service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.notification_module.service.NotificationService;
import org.slf4j.Logger;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
public class NotificationTask extends AbstractTask {
    private String message;
    private Long userId;
    private NotificationService notificationService;

    public NotificationTask(LocalDateTime executionTime, Logger logger) {
        super(executionTime, logger);
    }

    @Override
    public void execute() {
        notificationService.createAndSendNotification(userId ,message, true);
    }
}
