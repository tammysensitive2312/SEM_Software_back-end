package org.example.sem_backend.modules.notification_module.service.strategy;

import org.example.sem_backend.modules.notification_module.domain.entity.Notification;

public interface NotificationChannel {
    void send(Notification notification);
}
