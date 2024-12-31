package org.example.sem_backend.modules.notification_module.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SseEmitterServiceTest {

    private SseEmitterService sseEmitterService;

    @BeforeEach
    void setUp() {
        sseEmitterService = new SseEmitterService();
    }

    @Nested
    @DisplayName("Emitter Management Tests")
    class EmitterManagementTests {
        @Test
        @DisplayName("Add emitter for a new user")
        void testAddEmitterForNewUser() {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter = mock(SseEmitter.class);

            // Act
            sseEmitterService.addEmitter(userId, emitter);

            // Use reflection to access the private emittersMap
            try {
                java.lang.reflect.Field field = SseEmitterService.class.getDeclaredField("emittersMap");
                field.setAccessible(true);
                var emittersMap = (java.util.concurrent.ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>>) field.get(sseEmitterService);

                // Assert
                assertTrue(emittersMap.containsKey(userId));
                assertTrue(emittersMap.get(userId).contains(emitter));
                assertEquals(1, emittersMap.get(userId).size());
            } catch (Exception e) {
                fail("Reflection failed", e);
            }
        }

        @Test
        @DisplayName("Add multiple emitters for the same user")
        void testAddMultipleEmittersForUser() {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);

            // Act
            sseEmitterService.addEmitter(userId, emitter1);
            sseEmitterService.addEmitter(userId, emitter2);

            // Use reflection to access the private emittersMap
            try {
                java.lang.reflect.Field field = SseEmitterService.class.getDeclaredField("emittersMap");
                field.setAccessible(true);
                var emittersMap = (java.util.concurrent.ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>>) field.get(sseEmitterService);

                // Assert
                assertTrue(emittersMap.containsKey(userId));
                assertTrue(emittersMap.get(userId).contains(emitter1));
                assertTrue(emittersMap.get(userId).contains(emitter2));
                assertEquals(2, emittersMap.get(userId).size());
            } catch (Exception e) {
                fail("Reflection failed", e);
            }
        }

        @Test
        @DisplayName("Remove emitter for a user")
        void testRemoveEmitter() {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter = mock(SseEmitter.class);
            sseEmitterService.addEmitter(userId, emitter);

            // Act
            sseEmitterService.removeEmitter(userId, emitter);

            // Use reflection to access the private emittersMap
            try {
                java.lang.reflect.Field field = SseEmitterService.class.getDeclaredField("emittersMap");
                field.setAccessible(true);
                var emittersMap = (java.util.concurrent.ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>>) field.get(sseEmitterService);

                // Assert
                assertFalse(emittersMap.containsKey(userId));
            } catch (Exception e) {
                fail("Reflection failed", e);
            }
        }

        @Test
        @DisplayName("Remove emitter when multiple emitters exist")
        void testRemoveOneEmitterWithMultipleEmitters() {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);
            sseEmitterService.addEmitter(userId, emitter1);
            sseEmitterService.addEmitter(userId, emitter2);

            // Act
            sseEmitterService.removeEmitter(userId, emitter1);

            // Use reflection to access the private emittersMap
            try {
                java.lang.reflect.Field field = SseEmitterService.class.getDeclaredField("emittersMap");
                field.setAccessible(true);
                var emittersMap = (java.util.concurrent.ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>>) field.get(sseEmitterService);

                // Assert
                assertTrue(emittersMap.containsKey(userId));
                assertFalse(emittersMap.get(userId).contains(emitter1));
                assertTrue(emittersMap.get(userId).contains(emitter2));
                assertEquals(1, emittersMap.get(userId).size());
            } catch (Exception e) {
                fail("Reflection failed", e);
            }
        }
    }

    @Nested
    @DisplayName("Notification Sending Tests")
    class NotificationSendingTests {
        @Test
        @DisplayName("Send notification to a single emitter")
        void testSendNotificationToSingleEmitter() throws IOException {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter = mock(SseEmitter.class);
            sseEmitterService.addEmitter(userId, emitter);
            String message = "Test notification";

            // Act
            sseEmitterService.sendNotification(userId, message);

            // Assert
            verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
        }

        @Test
        @DisplayName("Send notification to multiple emitters")
        void testSendNotificationToMultipleEmitters() throws IOException {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);
            sseEmitterService.addEmitter(userId, emitter1);
            sseEmitterService.addEmitter(userId, emitter2);
            String message = "Test notification";

            // Act
            sseEmitterService.sendNotification(userId, message);

            // Assert
            verify(emitter1).send(any(SseEmitter.SseEventBuilder.class));
            verify(emitter2).send(any(SseEmitter.SseEventBuilder.class));
        }

        @Test
        @DisplayName("Handle IO exception when sending notification")
        void testSendNotificationWithIOException() throws IOException {
            // Arrange
            Long userId = 1L;
            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);
            sseEmitterService.addEmitter(userId, emitter1);
            sseEmitterService.addEmitter(userId, emitter2);
            String message = "Test notification";

            // Simulate IO exception for first emitter
            doThrow(new IOException("Simulated IO exception")).when(emitter1).send(Optional.ofNullable(any()));

            // Act
            sseEmitterService.sendNotification(userId, message);

            // Use reflection to access the private emittersMap
            try {
                java.lang.reflect.Field field = SseEmitterService.class.getDeclaredField("emittersMap");
                field.setAccessible(true);
                var emittersMap = (java.util.concurrent.ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>>) field.get(sseEmitterService);

                // Assert
                verify(emitter1).send(SseEmitter.event()
                        .name("notification")
                        .data(message));
                verify(emitter2).send(SseEmitter.event()
                        .name("notification")
                        .data(message));

                // Verify that the failed emitter was removed
                assertFalse(emittersMap.get(userId).contains(emitter1));
                assertTrue(emittersMap.get(userId).contains(emitter2));
            } catch (Exception e) {
                fail("Reflection failed", e);
            }
        }

        @Test
        @DisplayName("Send notification to non-existent user")
        void testSendNotificationToNonExistentUser() {
            // Arrange
            Long userId = 1L;
            String message = "Test notification";

            // Act
            // This should not throw any exception
            assertDoesNotThrow(() -> sseEmitterService.sendNotification(userId, message));
        }
    }
}