package org.example.sem_backend.modules.notification_module.service.concreteStragery;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.NotificationPersistenceException;
import org.example.sem_backend.common_module.exception.NotificationRecipientException;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.SseEmitterService;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InAppNotificationChannel implements NotificationChannel {
    private final NotificationRepository notificationRepository;
    private final SseEmitterService emitterService;

    /**
     * {@link Notification}
     */
    @Override
    public void send(Notification notification) {
        if (notification.getRecipients() == null || notification.getRecipients().isEmpty()) {
            throw new NotificationRecipientException("Notification must have at least one recipient");
        }

        try {
            Notification savedNotification = notificationRepository.save(notification);
            System.out.println("In-App Notification created: " + savedNotification);

            for (Long recipientId : notification.getRecipients()) {
                String message = savedNotification.getMessage();
                emitterService.sendNotification(recipientId, message);
            }
        } catch (Exception e) {
            throw new NotificationPersistenceException("Failed to save notification", e);
        }
    }
}
