package org.example.sem_backend.modules.notification_module.service;

import io.github.bucket4j.Bucket;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationResponse;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;
import org.example.sem_backend.modules.notification_module.domain.mapper.NotificationMapper;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.strategy.NotificationChannel;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private NotificationChannel notificationChannel;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Bucket bucket;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private NotificationResponse testNotificationResponse;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setMessage("Test message");
        testNotification.setType(NotificationType.IN_APP);
        testNotification.setRead(false);
        testNotification.setRecipients(new HashSet<>(Collections.singletonList(1L)));

        testNotificationResponse = new NotificationResponse(1L, "Test message",
                LocalDateTime.now().toString(), false);

        notificationService = new NotificationService(notificationRepository, mapper, userRepository,List.of(notificationChannel), kafkaTemplate);
    }

    @Test
    void createAndSendNotification_SingleUser_Success() {
        // Given
        Long userId = 1L;
        String message = "Test message";

        // When
        notificationService.createAndSendNotification(userId, message, true);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createAndSendNotification_SingleUser_RateLimitExceeded() {
        // Given
        Long userId = 1L;
        String message = "Test message";
        when(bucket.tryConsume(1)).thenReturn(false);

        // When
        notificationService.createAndSendNotification(userId, message, true);

        // Then
        verify(kafkaTemplate, times(1)).send(eq("notification_topic"), any(String.class));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createAndSendNotification_MultipleUsers_Success() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);
        String message = "Test message";

        // When
        notificationService.createAndSendNotification(userIds, message, true);

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createAndSendNotification_MultipleUsers_EmptyList() {
        // Given
        List<Long> userIds = Collections.emptyList();
        String message = "Test message";

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.createAndSendNotification(userIds, message, true));
    }

    @Test
    void getUnreadNotifications_Success() {
        // Given
        Long userId = 1L;
        List<Notification> notifications = Collections.singletonList(testNotification);
        when(notificationRepository.findByRecipientsContainingAndIsReadFalse(userId))
                .thenReturn(notifications);
        when(mapper.toDtoResponse(any(Notification.class)))
                .thenReturn(testNotificationResponse);

        // When
        List<NotificationResponse> result = notificationService.getUnreadNotifications(userId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(notificationRepository, times(1))
                .findByRecipientsContainingAndIsReadFalse(userId);
    }

    @Test
    void getAllNotifications_Success() {
        // Given
        Long userId = 1L;
        List<Notification> notifications = Collections.singletonList(testNotification);
        when(notificationRepository.findByRecipientsContaining(userId))
                .thenReturn(notifications);
        when(mapper.toDtoResponse(any(Notification.class)))
                .thenReturn(testNotificationResponse);

        // When
        List<NotificationResponse> result = notificationService.getAllNotifications(userId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(notificationRepository, times(1))
                .findByRecipientsContaining(userId);
    }

    @Test
    void markAsRead_Success() {
        // Given
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(testNotification));

        // When
        notificationService.markAsRead(notificationId);

        // Then
        assertTrue(testNotification.isRead());
        assertNotNull(testNotification.getReadAt());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void markAsRead_NotificationNotFound() {
        // Given
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(notificationId));
    }
}