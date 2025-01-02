package org.example.sem_backend.modules.borrowing_module.service.Impl;

import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomSchedule;
import org.example.sem_backend.modules.borrowing_module.domain.entity.TransactionsLog;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.RoomBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.RoomScheduleRepository;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RoomBorrowRequestServiceTest {

    @Mock
    private RoomBorrowRequestRepository roomBorrowRequestRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomBorrowRequestMapper mapper;
    @Mock
    private RoomScheduleRepository scheduleRepository;
    @Mock
    private TransactionsLogRepository logRepository;

    @InjectMocks
    private RoomBorrowRequestService roomBorrowRequestService;

    private RoomBorrowRequestDTO validRequestDto;
    private User testUser;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testRoom = new Room();
        testRoom.setUniqueId(1L);

        validRequestDto = new RoomBorrowRequestDTO();
        validRequestDto.setUniqueId(1L);
        validRequestDto.setUserId(1L);
        validRequestDto.setRoomId(1L);
        validRequestDto.setStartTime(LocalDateTime.now().plusHours(1));
        validRequestDto.setEndTime(LocalDateTime.now().plusHours(2));
        validRequestDto.setComment("Test booking");
    }

    @Nested
    @DisplayName("Process Request Tests")
    class ProcessRequestTests {

        @Test
        @DisplayName("Should successfully process valid room booking request")
        void processValidRequest() {
            // Arrange
            when(roomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
            when(scheduleRepository.findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(anyLong(), any(), any()))
                    .thenReturn(new ArrayList<>());

            // Act
            assertDoesNotThrow(() -> roomBorrowRequestService.processRequest(validRequestDto));

            // Assert
            verify(roomBorrowRequestRepository).save(any(RoomBorrowRequest.class));
            verify(scheduleRepository).save(any(RoomSchedule.class));
            verify(logRepository).save(any(TransactionsLog.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when room not found")
        void processRequestRoomNotFound() {
            // Arrange
            when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> roomBorrowRequestService.processRequest(validRequestDto));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void processRequestUserNotFound() {
            // Arrange
            when(roomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> roomBorrowRequestService.processRequest(validRequestDto));
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when room status is BROKEN")
        void processRequestRoomBroken() {
            // Arrange
            RoomBorrowRequestDTO requestDto = new RoomBorrowRequestDTO();
            requestDto.setRoomId(1L);
            requestDto.setUserId(1L);
            requestDto.setStartTime(LocalDateTime.now().plusHours(1));
            requestDto.setEndTime(LocalDateTime.now().plusHours(2));

            Room brokenRoom = new Room();
            brokenRoom.setStatus(RoomStatus.BROKEN);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(brokenRoom));
            when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

            // Act & Assert
            assertThrows(ResourceConflictException.class,
                    () -> roomBorrowRequestService.processRequest(requestDto));

            verify(roomBorrowRequestRepository, never()).save(any(RoomBorrowRequest.class));
            verify(scheduleRepository, never()).save(any(RoomSchedule.class));
            verify(logRepository, never()).save(any(TransactionsLog.class));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate request with valid future booking time")
        void validateValidFutureRequest() {
            // Arrange
            when(scheduleRepository.findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(anyLong(), any(), any()))
                    .thenReturn(new ArrayList<>());

            // Act & Assert
            assertTrue(roomBorrowRequestService.validateRequest(validRequestDto));
        }

        @Test
        @DisplayName("Should throw exception for booking more than 2 weeks ahead")
        void validateTooFarFutureRequest() {
            // Arrange
            validRequestDto.setStartTime(LocalDateTime.now().plusWeeks(3));
            validRequestDto.setEndTime(LocalDateTime.now().plusWeeks(3).plusHours(1));

            // Act & Assert
            assertThrows(ResourceConflictException.class,
                    () -> roomBorrowRequestService.validateRequest(validRequestDto));
        }

        @Test
        @DisplayName("Should throw exception for conflicting bookings")
        void validateConflictingRequest() {
            // Arrange
            RoomBorrowRequest existingRequest = new RoomBorrowRequest();
            existingRequest.setUniqueID(2L);

            RoomSchedule conflictingSchedule = new RoomSchedule();
            conflictingSchedule.setRequest(existingRequest);

            List<RoomSchedule> conflictingSchedules = List.of(conflictingSchedule);

            when(scheduleRepository.findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(anyLong(), any(), any()))
                    .thenReturn(conflictingSchedules);

            // Act & Assert
            assertThrows(ResourceConflictException.class,
                    () -> roomBorrowRequestService.validateRequest(validRequestDto));
        }
    }

    @Nested
    @DisplayName("Update Request Tests")
    class UpdateRequestTests {

        @Test
        @DisplayName("Should successfully update valid request within 24 hours")
        void updateValidRequest() {
            // Arrange
            RoomBorrowRequest existingRequest = new RoomBorrowRequest();
            existingRequest.setCreateAt(LocalDateTime.now());

            when(roomBorrowRequestRepository.findById(anyLong())).thenReturn(Optional.of(existingRequest));
            when(scheduleRepository.findByRoomUniqueIdAndEndTimeAfterAndStartTimeBefore(anyLong(), any(), any()))
                    .thenReturn(new ArrayList<>());

            // Act
            assertDoesNotThrow(() -> roomBorrowRequestService.updateRequest(validRequestDto));

            // Assert
            verify(roomBorrowRequestRepository).save(any(RoomBorrowRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when updating request after 24 hours")
        void updateRequestAfter24Hours() {
            // Arrange
            RoomBorrowRequest existingRequest = new RoomBorrowRequest();
            existingRequest.setCreateAt(LocalDateTime.now().minusDays(2));

            when(roomBorrowRequestRepository.findById(anyLong())).thenReturn(Optional.of(existingRequest));

            // Act & Assert
            assertThrows(ResourceConflictException.class,
                    () -> roomBorrowRequestService.updateRequest(validRequestDto));
        }
    }

    @Nested
    @DisplayName("Delete Requests Tests")
    class DeleteRequestsTests {

        @Test
        @DisplayName("Should successfully delete multiple requests")
        void deleteValidRequests() {
            // Arrange
            List<Long> requestIds = List.of(1L, 2L);
            List<RoomBorrowRequest> requests = List.of(new RoomBorrowRequest(), new RoomBorrowRequest());

            when(roomBorrowRequestRepository.findAllById(requestIds)).thenReturn(requests);

            // Act
            assertDoesNotThrow(() -> roomBorrowRequestService.deleteRequestsByIds(requestIds));

            // Assert
            verify(logRepository).deleteByRoomRequestIds(requestIds);
            verify(roomBorrowRequestRepository).deleteAll(requests);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent requests")
        void deleteNonExistentRequests() {
            // Arrange
            List<Long> requestIds = List.of(999L, 1000L);
            when(roomBorrowRequestRepository.findAllById(requestIds)).thenReturn(new ArrayList<>());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> roomBorrowRequestService.deleteRequestsByIds(requestIds));
        }
    }

    @Nested
    @DisplayName("Get Requests Tests")
    class GetRequestsTests {

        @Test
        @DisplayName("Should successfully get user requests")
        void getUserRequests() {
            GetRoomRequestDTO mockRequest = new GetRoomRequestDTO();
            mockRequest.setStartTime(LocalDateTime.now().plusDays(2));
            // Arrange
            Page<GetRoomRequestDTO> expectedPage = new PageImpl<>(List.of(mockRequest));
            when(roomBorrowRequestRepository.findRequestsWithSchedules(
                    anyLong(), isNull(), any(), any(), any(Pageable.class)))
                    .thenReturn(expectedPage);

            // Act
            Page<GetRoomRequestDTO> result = roomBorrowRequestService.getUserRequests(
                    1L, LocalDate.now(), LocalDate.now().plusDays(1), Pageable.unpaged());

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().getFirst().isCancelable());
        }

        @Test
        @DisplayName("Should successfully get admin requests")
        void getAdminRequests() {
            GetRoomRequestDTO mockRequest = new GetRoomRequestDTO();
            mockRequest.setStartTime(LocalDateTime.now().plusDays(2));
            // Arrange
            Page<GetRoomRequestDTO> expectedPage = new PageImpl<>(List.of(mockRequest));
            when(roomBorrowRequestRepository.findRequestsWithSchedules(
                    isNull(), anyString(), any(), any(), any(Pageable.class)))
                    .thenReturn(expectedPage);

            // Act
            Page<GetRoomRequestDTO> result = roomBorrowRequestService.getAdminRequests(
                    "admin@example.com", LocalDate.now(), LocalDate.now().plusDays(1), Pageable.unpaged());

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }
    }
}