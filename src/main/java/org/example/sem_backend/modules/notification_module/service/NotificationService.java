package org.example.sem_backend.modules.notification_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationRequest;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;
import org.example.sem_backend.modules.notification_module.domain.mapper.NotificationMapper;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final List<NotificationChannel> notificationChannels;
    private final NotificationMapper mapper;

    @EventListener
    public void handleEquipmentBorrowedEvent(EquipmentBorrowedEvent event) {
        Long userId = event.getUserId();
        Long requestId = event.getRequestId();
        String message = "Đơn mượn #" + requestId + " của bạn đã được duyệt thành công.";

        createAndSendNotification(userId, message, true);
    }

    private void createAndSendNotification(Long userId, String message, boolean needSave) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(NotificationType.IN_APP);
        notification.setRead(false);
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

    @Transactional
    public void sendInAppNotificationAndSave(Long userId, String message) {
        createAndSendNotification(userId, message, true);
    }

    public List<NotificationRequest> getUnreadNotifications(Long userId) {

        List<NotificationRequest> notifications = notificationRepository.findByRecipientsContainingAndIsReadFalse(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return notifications;
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
