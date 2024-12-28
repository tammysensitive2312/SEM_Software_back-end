package org.example.sem_backend.modules.rooms_module;

import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.mapper.RoomMapper;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRoom_WhenRoomNameExists_ThrowsException() {
        RoomRequest request = new RoomRequest();
        request.setRoomName("Room 101");

        when(roomRepository.existsByRoomName(anyString())).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> roomService.addRoom(request)
        );

        assertEquals("Room name already exists", exception.getMessage());
    }

    @Test
    void updateRoom_WhenRoomExists_UpdatesSuccessfully() {
        Room room = new Room();
        RoomRequest request = new RoomRequest();
        request.setRoomName("Room 102");

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

        roomService.updateRoom(request, 1L);

        verify(roomMapper, times(1)).partialUpdate(request, room);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void updateRoom_WhenRoomDoesNotExist_ThrowsException() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.updateRoom(new RoomRequest(), 1L)
        );

        assertEquals("Room not found", exception.getMessage());
    }
}
