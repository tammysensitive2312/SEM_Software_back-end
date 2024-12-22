package org.example.sem_backend.modules.notification_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.common.event.GenericEvent;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationRequest;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;
import org.example.sem_backend.modules.notification_module.domain.mapper.NotificationMapper;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.example.sem_backend.modules.user_module.domain.entity.ERole;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final List<NotificationChannel> notificationChannels;
    private final NotificationMapper mapper;

    @EventListener
    public void handleEquipmentBorrowedEvent(EquipmentBorrowedEvent event) {
        Long userId = event.getUserId();
        Long requestId = event.getRequestId();
        String message = "Đơn mượn #" + requestId + " của bạn đã được duyệt thành công.";

        createAndSendNotification(userId, message, true);
    }

    @EventListener
    public void handleRoomStatusChangedEvent(GenericEvent<List<Long>> event) {
        List<Long> userIds = event.getData();
        String message = "Phòng hiện đang gặp sự cố, admin đang xử lý";
        createAndSendNotification(userIds, message, true);
    }

    /**
     * Creates a new notification and sends it through all available notification channels.
     * If specified, the notification is also saved to the database.
     *
     * @param userId    The ID of the user to whom the notification is being sent.
     * @param message   The content of the notification message.
     * @param needSave  A boolean flag indicating whether the notification should be saved to the database.
     *                  If true, the notification is persisted; if false, it is only sent through channels.
     */
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

    private void createAndSendNotification(List<Long> userIds, String message, boolean needSave) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("userIds cannot be null or empty");
        }

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(NotificationType.IN_APP);
        notification.setRead(false);
        notification.setRecipients(new HashSet<>(userIds));

        if (needSave) {
            notificationRepository.save(notification);
        }

        // Gửi thông báo qua tất cả các kênh
        for (NotificationChannel channel : notificationChannels) {
            channel.send(notification);
        }
    }

    // method run asynchronous to send notification to all user
    @Async
    @Transactional
    public CompletableFuture<Void> sendNotificationToAdminUser(NotificationRequest request) {
        return CompletableFuture.runAsync(() -> sendMessage(() ->
                        userRepository.findIdByRole(ERole.ROLE_ADMIN),
                        request.getMessage()));
    }


    /**
     * Sends a message to multiple users identified by their user IDs.
     * This method retrieves a list of user IDs from the provided UserIdProvider,
     * validates the list, and then sends a notification to each user.
     *
     * @param provider {@link UserIdProvider} A Functional Interface that supplies the list of user IDs to send the message to.
     * @param message The content of the message to be sent to the users.
     * @throws IllegalArgumentException If the list of user IDs is null or empty.
     */
    void sendMessage(UserIdProvider provider, String message) {
        List<Long> receipientIds = provider.getUserIds();

        if (receipientIds == null || receipientIds.isEmpty()) {
            throw new IllegalArgumentException("UserId list cannot be null or empty");
        }

        createAndSendNotification(receipientIds, message, true);
    }

    public List<NotificationRequest> getUnreadNotifications(Long userId) {

        List<NotificationRequest> notifications = notificationRepository.findByRecipientsContainingAndIsReadFalse(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return notifications;
    }

    public Page<NotificationRequest> getAllNotifications(Long userId, Pageable pageable) {
        Page<NotificationRequest> notifications = notificationRepository.findByRecipientsContaining(userId, pageable)
                .map(mapper::toDto);

        return notifications;
    }

    // logic đánh dấu thông báo đã đọc
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
