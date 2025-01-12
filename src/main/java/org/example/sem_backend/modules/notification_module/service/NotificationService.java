package org.example.sem_backend.modules.notification_module.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationRequest;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationResponse;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;
import org.example.sem_backend.modules.notification_module.domain.mapper.NotificationMapper;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.example.sem_backend.modules.user_module.domain.entity.ERole;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final UserRepository userRepository;
    private final List<NotificationChannel> notificationChannels;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private NotificationProducer producer;

    @PostConstruct
    private void init() {
        // Khởi tạo producer khi service được tạo
        this.producer = new NotificationProducer(kafkaTemplate);
    }

    // Rate Limiting Bucket
    private final Bucket bucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
            .build();

    /**
     * Tạo và gửi thông báo cho một người dùng.
     */
    public void createAndSendNotification(Long userId, String message, boolean needSave) {
        if (tryConsumeRateLimit()) {
            Notification notification = buildNotification(userId, message);

            if (needSave) {
                saveNotification(notification);
            }

            sendNotificationAsync(notification);
        } else {
            producer.sendToKafka(userId, message);
        }
    }

    /**
     * Tạo và gửi thông báo cho nhiều người dùng.
     */
    public void createAndSendNotification(List<Long> userIds, String message, boolean needSave) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs cannot be null or empty");
        }

        if (tryConsumeRateLimit()) {
            Notification notification = buildNotification(userIds, message);

            if (needSave) {
                saveNotification(notification);
            }

            sendNotificationAsync(notification);
        } else {
            producer.sendToKafka(userIds, message);
        }
    }

    /**
     * Xây dựng thông báo cho một người dùng.
     */
    private Notification buildNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(NotificationType.IN_APP);
        notification.setRead(false);
        notification.setRecipients(new HashSet<>());
        notification.getRecipients().add(userId);
        return notification;
    }

    /**
     * Xây dựng thông báo cho nhiều người dùng.
     */
    private Notification buildNotification(List<Long> userIds, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(NotificationType.IN_APP);
        notification.setRead(false);
        notification.setRecipients(new HashSet<>(userIds));
        return notification;
    }

    /**
     * Lưu thông báo vào cơ sở dữ liệu.
     */
    private void saveNotification(Notification notification) {
        try {
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Failed to save notification", e);
        }
    }

    /**
     * Gửi thông báo bất đồng bộ qua tất cả các kênh.
     */
    private void sendNotificationAsync(Notification notification) {
        for (NotificationChannel channel : notificationChannels) {
            CompletableFuture.runAsync(() -> {
                try {
                    channel.send(notification);
                } catch (Exception e) {
                    log.error("Failed to send notification via channel: {}", channel.getClass().getSimpleName(), e);
                }
            });
        }
    }

    /**
     * Kiểm tra và tiêu thụ giới hạn rate limit.
     */
    private boolean tryConsumeRateLimit() {
        return bucket.tryConsume(1);
    }

    /**
     * Inner Class: NotificationProducer
     * Chịu trách nhiệm đẩy thông báo vượt quá giới hạn vào Kafka.
     */
    public class NotificationProducer {
        private final KafkaTemplate<String, String> kafkaTemplate;

        public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
            this.kafkaTemplate = kafkaTemplate;
        }

        public void sendToKafka(Long userId, String message) {
            String notificationJson = String.format("{\"userId\": %d, \"message\": \"%s\"}", userId, message);
            kafkaTemplate.send("notification_topic", notificationJson);
            log.info("Notification sent to Kafka for userId: {} with message: {}", userId, message);
        }

        public void sendToKafka(List<Long> userIds, String message) {
            String notificationJson = String.format("{\"userIds\": %s, \"message\": \"%s\"}", userIds, message);
            kafkaTemplate.send("notification_topic", notificationJson);
            log.info("Notification sent to Kafka for userIds: {} with message: {}", userIds, message);
        }
    }

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

    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientsContainingAndIsReadFalse(userId)
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList())
                .reversed();
    }

    /**
     * Return all notifications for a user.
     */
    public List<NotificationResponse> getAllNotifications(Long userId) {
        return notificationRepository.findByRecipientsContaining(userId)
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList())
                .reversed();
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
