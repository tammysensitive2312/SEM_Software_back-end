package org.example.sem_backend.modules.notification_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final List<NotificationChannel> notificationChannels;

    @EventListener
    public void handleEquipmentBorrowedEvent(EquipmentBorrowedEvent event) {
        Long userId = event.getUserId();
        Long requestId = event.getRequestId();
        String message = "Đơn mượn #" + requestId + " của bạn đã được duyệt thành công.";

        createAndSendNotification(userId, "Đơn mượn được duyệt", message, true);
    }

    @Transactional
    public void sendInAppNotificationAndSave(Long userId, String message) {
        createAndSendNotification(userId, "Thông báo mới", message, true);
    }

    private void createAndSendNotification(Long userId, String subject, String message, boolean needSave) {
        Notification notification = new Notification();
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setType(NotificationType.IN_APP);
        notification.setRead(false);
        notification.setCreateAt(LocalDateTime.now());
        notification.setRecipients(new HashSet<>());
        notification.getRecipients().add(userId);

        if (needSave) {
            notificationRepository.save(notification);
        }

        // Gửi thông báo qua tất cả các kênh
        for (NotificationChannel channel : notificationChannels) {
            channel.send(notification);
        }
    }


    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientsContainingAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

}
