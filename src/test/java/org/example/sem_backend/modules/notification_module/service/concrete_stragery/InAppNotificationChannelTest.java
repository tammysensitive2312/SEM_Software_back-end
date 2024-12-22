package org.example.sem_backend.modules.notification_module.service.concrete_stragery;

import org.example.sem_backend.common_module.exception.NotificationPersistenceException;
import org.example.sem_backend.common_module.exception.NotificationRecipientException;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.repository.NotificationRepository;
import org.example.sem_backend.modules.notification_module.service.SseEmitterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InAppNotificationChannelTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SseEmitterService emitterService;

    private InAppNotificationChannel notificationChannel;

    @BeforeEach
    void setUp() {
        notificationChannel = new InAppNotificationChannel(notificationRepository, emitterService);
    }

    @Nested
    @DisplayName("Normal Scenario Tests")
    class NormalScenarioTests {
        @Test
        @DisplayName("Successfully send notification to multiple recipients")
        void testSuccessfulNotificationSend() {
            // Arrange
            Notification notification = new Notification();
            Set<Long> recipients = new HashSet<>(Set.of(1L, 2L, 3L));
            notification.setRecipients(recipients);
            notification.setMessage("Test notification");

            Notification savedNotification = new Notification();
            savedNotification.setId(1L);
            savedNotification.setMessage("Test notification");
            savedNotification.setRecipients(recipients);

            when(notificationRepository.save(notification)).thenReturn(savedNotification);

            // Act
            notificationChannel.send(notification);

            // Assert
            verify(notificationRepository).save(notification);
            verify(emitterService).sendNotification(1L, "Test notification");
            verify(emitterService).sendNotification(2L, "Test notification");
            verify(emitterService).sendNotification(3L, "Test notification");
        }

        @Test
        @DisplayName("Successfully send notification to single recipient")
        void testSuccessfulNotificationSendToSingleRecipient() {
            // Arrange
            Notification notification = new Notification();
            Set<Long> recipients = Collections.singleton(1L);
            notification.setRecipients(recipients);
            notification.setMessage("Single recipient notification");

            Notification savedNotification = new Notification();
            savedNotification.setId(1L);
            savedNotification.setMessage("Single recipient notification");
            savedNotification.setRecipients(recipients);

            when(notificationRepository.save(notification)).thenReturn(savedNotification);

            // Act
            notificationChannel.send(notification);

            // Assert
            verify(notificationRepository).save(notification);
            verify(emitterService).sendNotification(1L, "Single recipient notification");
        }
    }

    @Nested
    @DisplayName("Abnormal Scenario Tests")
    class AbnormalScenarioTests {
        @Test
        @DisplayName("Throw exception when no recipients are specified")
        void testNoRecipientsSpecified() {
            // Arrange
            Notification notification = new Notification();
            notification.setRecipients(null);

            // Act & Assert
            assertThrows(NotificationRecipientException.class, () -> notificationChannel.send(notification));

            verify(notificationRepository, never()).save(any());
            verify(emitterService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Throw exception when empty recipients set is provided")
        void testEmptyRecipientsSet() {
            // Arrange
            Notification notification = new Notification();
            notification.setRecipients(Collections.emptySet());

            // Act & Assert
            assertThrows(NotificationRecipientException.class, () -> notificationChannel.send(notification));

            verify(notificationRepository, never()).save(any());
            verify(emitterService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Handle repository save failure")
        void testRepositorySaveFailure() {
            // Arrange
            Notification notification = new Notification();
            Set<Long> recipients = Collections.singleton(1L);
            notification.setRecipients(recipients);

            when(notificationRepository.save(notification)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(NotificationPersistenceException.class, () -> notificationChannel.send(notification));

            verify(notificationRepository).save(notification);
            verify(emitterService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Handle emitter service failure")
        void testEmitterServiceFailure() {
            // Arrange
            Notification notification = new Notification();
            Set<Long> recipients = Collections.singleton(1L);
            notification.setRecipients(recipients);
            notification.setMessage("Test notification");

            Notification savedNotification = new Notification();
            savedNotification.setId(1L);
            savedNotification.setMessage("Test notification");
            savedNotification.setRecipients(recipients);

            when(notificationRepository.save(notification)).thenReturn(savedNotification);
            doThrow(new RuntimeException("Emitter service error")).when(emitterService).sendNotification(1L, "Test notification");

            // Act & Assert
            assertThrows(NotificationPersistenceException.class, () -> notificationChannel.send(notification));

            verify(notificationRepository).save(notification);
            verify(emitterService).sendNotification(1L, "Test notification");
        }
    }
}