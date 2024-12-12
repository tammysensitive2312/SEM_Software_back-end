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
     * Sends a notification to specified recipients using ServerSentEvent mechanism.
     *
     * <p>This method handles the complete notification sending process, which includes:
     * <ul>
     *   <li>Validating the presence of recipients</li>
     *   <li>Persisting the notification in the database</li>
     *   <li>Sending real-time notifications to each recipient via ServerSentEvent</li>
     * </ul>
     * </p>
     *
     * @param notification The notification to be sent, containing message and recipient details
     * @throws NotificationRecipientException If no recipients are specified for the notification
     * @throws NotificationPersistenceException If there is an error saving or sending the notification
     *
     * @see Notification
     * @see NotificationRepository
     * @see SseEmitterService
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
                System.out.println("Recipient ID: " + recipientId);
                String message = savedNotification.getMessage();
                emitterService.sendNotification(recipientId, message);
            }
        } catch (Exception e) {
            throw new NotificationPersistenceException("Failed to save notification with error :" + e);
        }
    }
}
