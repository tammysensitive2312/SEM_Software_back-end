package org.example.sem_backend.modules.notification_module.service;

import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.modules.notification_module.domain.dto.NotificationRequest;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.domain.mapper.NotificationMapper;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.example.sem_backend.modules.user_module.domain.entity.ERole;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationChannel notificationChannel;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(notificationRepository, userRepository,List.of(notificationChannel), mapper);
    }

    @Test
    void handleEquipmentBorrowedEvent_shouldCreateAndSendNotification() {
        // Arrange
        EquipmentBorrowedEvent event = new EquipmentBorrowedEvent(this, 1L, 2L);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Act
        notificationService.handleEquipmentBorrowedEvent(event);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());
        verify(notificationChannel).send(notificationCaptor.capture());

        Notification notification = notificationCaptor.getValue();
        assertEquals("Đơn mượn #1 của bạn đã được duyệt thành công.", notification.getMessage());
        assertTrue(notification.getRecipients().contains(2L));
        assertFalse(notification.isRead());
    }

    @Test
    void getUnreadNotifications_shouldReturnNotifications() {
        // Arrange
        Long userId = 2L;
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRecipients(new HashSet<>(Collections.singletonList(userId)));
        notification.setRead(false);

        NotificationRequest request = new NotificationRequest(
                notification.getMessage(),
                false);

        when(notificationRepository.findByRecipientsContainingAndIsReadFalse(userId))
                .thenReturn(List.of(notification));

        when(mapper.toDto(notification)).thenReturn(request);
        // Act
        List<NotificationRequest> unreadNotifications = notificationService.getUnreadNotifications(userId);

        // Assert
        assertNotNull(unreadNotifications);
        assertEquals(1, unreadNotifications.size());
        assertEquals(request, unreadNotifications.getFirst());
    }

    @Test
    void markAsRead_shouldUpdateNotification() {
        // Arrange
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setRead(false);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        assertTrue(notification.isRead());
        assertNotNull(notification.getReadAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_shouldThrowExceptionWhenNotificationNotFound() {
        // Arrange
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificationService.markAsRead(notificationId));
        assertEquals("Notification not found", exception.getMessage());
    }

    @Test
    void sendNotificationToAdminUser_shouldSendToAdmins() {
        // Arrange
        NotificationRequest request = new NotificationRequest("Test message", false);
        List<Long> adminIds = List.of(1L, 2L);
        when(userRepository.findIdByRole(ERole.ROLE_ADMIN)).thenReturn(adminIds);

        // Act
        notificationService.sendNotificationToAdminUser(request).join();

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, atLeastOnce()).save(captor.capture());

        Notification savedNotification = captor.getValue();
        assertEquals("Test message", savedNotification.getMessage());
        assertTrue(savedNotification.getRecipients().containsAll(adminIds),
                "Recipients should contain all admin IDs");
    }


    @Test
    void sendMessage_shouldSendToValidUserIds() {
        // Arrange
        List<Long> userIds = List.of(1L, 2L);
        String message = "Hello Users!";

        // Act
        notificationService.sendMessage(() -> userIds, message);

        // Assert
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification savedNotification = captor.getValue();
        assertEquals(message, savedNotification.getMessage());
        assertTrue(savedNotification.getRecipients().containsAll(userIds),
                "Recipients should contain all user IDs");
    }


    @Test
    void sendMessage_shouldThrowExceptionForEmptyUserList() {
        // Arrange
        String message = "Hello Users!";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                notificationService.sendMessage(Collections::emptyList, message));
        assertEquals("UserId list cannot be null or empty", exception.getMessage());
    }

    @Test
    void sendMessage_shouldHandleNullUserList() {
        // Arrange
        String message = "Hello Users!";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                notificationService.sendMessage(() -> null, message));
        assertEquals("UserId list cannot be null or empty", exception.getMessage());
    }
}