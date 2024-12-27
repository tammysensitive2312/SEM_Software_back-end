package org.example.sem_backend.modules.borrowing_module.service.listener;

import org.example.sem_backend.common_module.common.event.GenericEvent;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomStatusChangedListenerTest {

    @Mock
    private RoomBorrowRequestRepository borrowingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomStatusChangedListener roomStatusChangedListener;

    @Captor
    private ArgumentCaptor<GenericEvent<List<Long>>> eventCaptor;

    @Test
    @DisplayName("Should publish event when source is RoomService and there are users with future bookings")
    void shouldPublishEventWhenValidSourceAndHasUsers() {

        GenericEvent<Long> event = new GenericEvent<>(roomService, 1L);
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        when(borrowingRepository.findUserIdsWithBookingsAfter(any(LocalDateTime.class), eq(1L)))
                .thenReturn(userIds);

        // Act
        roomStatusChangedListener.handleRoomStatusChange(event);

        // Assert
        verify(eventPublisher).publishEvent(argThat(evt ->
                evt instanceof GenericEvent &&
                        ((GenericEvent<?>) evt).getData().equals(userIds)
        ));
    }

    @Test
    @DisplayName("Should not publish event when source is RoomService but no users with future bookings")
    void shouldNotPublishEventWhenValidSourceButNoUsers() {
        GenericEvent<Long> event = new GenericEvent<>(roomService, 1L);
        List<Long> emptyUserIds = Collections.emptyList();

        when(borrowingRepository.findUserIdsWithBookingsAfter(any(LocalDateTime.class), eq(1L)))
                .thenReturn(emptyUserIds);

        // Act
        roomStatusChangedListener.handleRoomStatusChange(event);

        // Assert
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should log error when source is not RoomService")
    void shouldLogErrorWhenInvalidSource() {
        // Arrange
        Object invalidSource = new Object();
        GenericEvent<Long> event = new GenericEvent<>(invalidSource, 1L);

        // Act
        roomStatusChangedListener.handleRoomStatusChange(event);

        // Assert
        verify(borrowingRepository, never()).findUserIdsWithBookingsAfter(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should handle null roomId gracefully when source is RoomService")
    void shouldHandleNullRoomIdGracefully() {
        GenericEvent<Long> event = new GenericEvent<>(roomService, null);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                roomStatusChangedListener.handleRoomStatusChange(event));

        // Assert
        assertEquals("roomId in event context is null ", exception.getMessage());
        verify(eventPublisher, never()).publishEvent(any());
    }
}