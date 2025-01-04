package org.example.sem_backend.modules.borrowing_module.service.Impl;

import org.apache.coyote.BadRequestException;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentRequestDeniedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentReturnedEvent;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowItemDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDenyDto;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestDetailMapper;
import org.example.sem_backend.modules.borrowing_module.domain.mapper.EquipmentBorrowRequestMapper;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
import org.example.sem_backend.modules.borrowing_module.repository.detail.EquipmentBorrowRequestDetailRepository;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EquipmentBorrowRequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EquipmentBorrowRequestRepository requestRepository;
    @Mock
    private EquipmentBorrowRequestDetailRepository borrowRequestDetailRepository;
    @Mock
    private EquipmentDetailRepository equipmentDetailRepository;
    @Mock
    private EquipmentRepository equipmentRepository;
    @Mock
    private TransactionsLogRepository logRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private EquipmentBorrowRequestMapper requestMapper;
    @Mock
    private EquipmentBorrowRequestDetailMapper detailMapper;

    @InjectMocks
    private EquipmentBorrowRequestService service;

    User user;
    Equipment equipment;
    Long userId;
    String equipmentName;
    EquipmentBorrowRequestDTO requestDto;
    EquipmentBorrowItemDTO itemDto;
    EquipmentBorrowRequest request;
    EquipmentBorrowRequestDetail detail;
    EquipmentDetail equipmentDetail1;
    EquipmentDetail equipmentDetail2;

    @BeforeEach
    void setUp() {
        // tạo các mock repository
        MockitoAnnotations.openMocks(this);

        userId = 1L;
        user = Utils.createUser(userId);

        equipmentName = "Laptop";
        equipment = Utils.createEquipment(equipmentName, 25);
        // Mock valid DTO
        requestDto = Utils.createValidBorrowRequestDTO(1L);
        // Create equipment borrow item DTO
        itemDto = Utils.createBorrowItemDTO(equipmentName, 2, "Good");

        request = Utils.createBorrowRequest(user);

        detail = Utils.createBorrowRequestDetail(equipment, 2);

        equipmentDetail1 = Utils.createEquipmentDetail(detail.getEquipment(), 101L);
        equipmentDetail2 = Utils.createEquipmentDetail(detail.getEquipment(), 102L);
    }

    @Nested
    @DisplayName("Process request test")
    class ProcessRequestTest {
        @Test
        @DisplayName("Process request should successfully when valid input")
        void testProcessRequest_ShouldSucceed_WhenValidInput() {
            // Setup mocks with lenient matching to allow multiple calls
            lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            lenient().when(equipmentRepository.findByEquipmentName(equipmentName))
                    .thenReturn(Optional.of(equipment));
            when(requestMapper.toEntity(requestDto)).thenReturn(request);

            // Create equipment borrow item DTO
            EquipmentBorrowItemDTO itemDto = new EquipmentBorrowItemDTO();
            itemDto.setEquipmentName(equipmentName);
            itemDto.setQuantityBorrowed(2);
            itemDto.setConditionBeforeBorrow("Good");

            requestDto.setEquipmentItems(Collections.singletonList(itemDto));

            // Mock request repository save to return the saved request
            when(requestRepository.save(any(EquipmentBorrowRequest.class)))
                    .thenAnswer(invocation -> {
                        EquipmentBorrowRequest savedRequest = invocation.getArgument(0);
                        savedRequest.setUniqueID(100L); // Simulate setting an ID
                        return savedRequest;
                    });

            // Execute the method
            service.processRequest(requestDto);

            // Verify key interactions
            verify(userRepository).findById(userId);
            verify(requestRepository).save(argThat(request -> {
                assertThat(request.getUser()).isEqualTo(user);
                assertThat(request.getStatus()).isEqualTo(EquipmentBorrowRequest.Status.NOT_BORROWED);
                assertThat(request.getBorrowRequestDetails()).hasSize(1);

                EquipmentBorrowRequestDetail detail = request.getBorrowRequestDetails().getFirst();
                assertThat(detail.getEquipment()).isEqualTo(equipment);
                assertThat(detail.getQuantityBorrowed()).isEqualTo(2);
                assertThat(detail.getConditionBeforeBorrow()).isEqualTo("Good");

                return true;
            }));
        }


        @Test
        @DisplayName("Process request should throw ResourceNotFoundException when user not found")
        void testProcessRequest_ShouldThrowException_WhenUserNotFound() {
            requestDto.setEquipmentItems(Collections.singletonList(itemDto));
            // Setup mocks
            lenient().when(equipmentRepository.findByEquipmentName(equipmentName))
                    .thenReturn(Optional.of(equipment));

            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            ResourceConflictException ex = assertThrows(ResourceConflictException.class,
                    () -> service.processRequest(requestDto));

            assertEquals("User not found", ex.getMessage());

            verify(userRepository).findById(1L);
            verifyNoInteractions(logRepository);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("Process request throw IllegalArgumentException when equipment null")
        void testProcessRequest_ShouldThrowException_WhenEquipmentItemsIsNull() {
            requestDto.setEquipmentItems(null); // Equipment items là null
            // Execute & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.processRequest(requestDto));
            assertEquals("Equipment items cannot be null or empty", ex.getMessage());

            verifyNoInteractions(equipmentDetailRepository, userRepository, requestRepository, logRepository, eventPublisher);
        }
    }

    @Nested
    @DisplayName("Validate request test")
    class ValidationRequestTest {
        @Test
        @DisplayName("Validate return true when request valid")
        void testValidateRequest_ShouldReturnTrue_WhenRequestIsValid() {
            equipment.setUsableQuantity(10);

            when(requestRepository.hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class))).thenReturn(false);
            when(equipmentRepository.findByEquipmentName("Laptop")).thenReturn(Optional.of(equipment));

            // Mock valid DTO
            EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
            requestDto.setUserId(1L);
            requestDto.setEquipmentItems(List.of(
                    new EquipmentBorrowItemDTO("Laptop", 2, List.of("LPT001", "LPT002"), "Good condition")
            ));

            boolean isValid = service.validateRequest(requestDto);

            assertTrue(isValid);

            verify(requestRepository).hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class));
            verify(equipmentRepository).findByEquipmentName("Laptop");
        }


        @Test
        @DisplayName("Validate request return false when overdue exist")
        void testValidateRequest_ShouldReturnFalse_WhenOverdueRequestsExist() {
            when(requestRepository.hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class))).thenReturn(true);

            EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
            requestDto.setUserId(1L);

            boolean isValid = service.validateRequest(requestDto);

            assertFalse(isValid);

            verify(requestRepository).hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class));
            verifyNoInteractions(equipmentRepository);
        }

        @Test
        @DisplayName("Validate request return false when equipment unavailable")
        void testValidateRequest_ShouldReturnFalse_WhenEquipmentUnavailable() {
            when(requestRepository.hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class))).thenReturn(false);
            equipment.setUsableQuantity(0); // Không đủ số lượng
            when(equipmentRepository.findByEquipmentName("Laptop")).thenReturn(Optional.of(equipment));

            ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> service.validateRequest(requestDto));
            assertEquals("Not enough quantity for equipment: Laptop", ex.getMessage());

            verify(requestRepository).hasOverdueRequests(anyLong(), anyList(), any(LocalDate.class));
            verify(equipmentRepository).findByEquipmentName("Laptop");
        }
    }

    @Nested
    @DisplayName("Approve request test")
    class ApproveRequestTest {

        @Test
        @DisplayName("Approve request throw exception when request not found")
        void testApproveRequest_ShouldThrowException_WhenRequestNotFound() {
            when(requestRepository.findById(1L)).thenReturn(Optional.empty());
            // Execute & Assert
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.approveRequest(1L));
            assertEquals("Request not found", ex.getMessage());

            verify(requestRepository).findById(1L);
            verifyNoInteractions(equipmentDetailRepository, eventPublisher);
        }

        @Test
        void approveRequest_shouldThrowResourceConflictExceptionWhenNotEnoughEquipment() {
            // Arrange
            Long requestId = 1L;

            // Mock behavior for requestRepository
            when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

            // Add detail into request
            request.getBorrowRequestDetails().add(detail);

            // Mock behavior for equipmentDetailRepository
            // Trả về danh sách thiết bị khả dụng nhỏ hơn số lượng yêu cầu
            when(equipmentDetailRepository.findAvailableByEquipmentId(eq(equipment.getId()), any(Pageable.class)))
                    .thenReturn(List.of(equipmentDetail1)); // Chỉ có 1 thiết bị khả dụng, ít hơn 2 yêu cầu

            // Act & Assert
            ResourceConflictException exception = assertThrows(ResourceConflictException.class, () -> {
                service.approveRequest(requestId); // Replace `service` with your actual service object
            });

            // Kiểm tra nội dung thông báo lỗi
            assertEquals("Not enough available equipment for: " + equipment.getEquipmentName(), exception.getMessage());

            // Verify không lưu trạng thái request
            verify(requestRepository, never()).save(any());
            // Verify event không được phát
            verify(eventPublisher, never()).publishEvent(any());
        }

    }

    @Nested
    @DisplayName("Return request test")
    class ReturnRequestTest {
        @Test
        @DisplayName("Should successfully return equipment when all requests are valid")
        void returnEquipment_Success() {
            // Arrange
            List<Long> requestIds = Arrays.asList(1L, 2L);

            EquipmentBorrowRequest request1 = new EquipmentBorrowRequest();
            request1.setStatus(EquipmentBorrowRequest.Status.BORROWED);

            EquipmentBorrowRequest request2 = new EquipmentBorrowRequest();
            request2.setStatus(EquipmentBorrowRequest.Status.BORROWED);

            List<EquipmentBorrowRequest> requests = Arrays.asList(request1, request2);

            when(requestRepository.findAllById(requestIds)).thenReturn(requests);
            when(requestRepository.saveAll(any())).thenReturn(requests);

            // Act
            try {
                service.returnEquipment(requestIds);
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }

            // Assert
            verify(requestRepository).findAllById(requestIds);
            verify(requestRepository).saveAll(requests);
            verify(eventPublisher).publishEvent(any(EquipmentReturnedEvent.class));

            assertEquals(EquipmentBorrowRequest.Status.RETURNED, request1.getStatus());
            assertEquals(EquipmentBorrowRequest.Status.RETURNED, request2.getStatus());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no requests found")
        void returnEquipment_NoRequestsFound() {
            // Arrange
            List<Long> requestIds = Arrays.asList(1L, 2L);
            when(requestRepository.findAllById(requestIds)).thenReturn(Collections.emptyList());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.returnEquipment(requestIds)
            );

            assertEquals("No borrow requests found", exception.getMessage());
            assertEquals("EQUIPMENT-MODULE", exception.getModule());
            verify(requestRepository).findAllById(requestIds);
            verify(requestRepository, never()).saveAll(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when some requests are not in BORROWED status")
        void returnEquipment_InvalidStatus() {
            // Arrange
            List<Long> requestIds = Arrays.asList(1L, 2L);

            EquipmentBorrowRequest request1 = new EquipmentBorrowRequest();
            request1.setStatus(EquipmentBorrowRequest.Status.BORROWED);

            EquipmentBorrowRequest request2 = new EquipmentBorrowRequest();
            request2.setStatus(EquipmentBorrowRequest.Status.RETURNED); // Invalid status

            List<EquipmentBorrowRequest> requests = Arrays.asList(request1, request2);

            when(requestRepository.findAllById(requestIds)).thenReturn(requests);

            // Act & Assert
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> service.returnEquipment(requestIds)
            );

            assertEquals("Some requests are not in BORROWED status", exception.getMessage());
            verify(requestRepository).findAllById(requestIds);
            verify(requestRepository, never()).saveAll(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when request ids list is null")
        void returnEquipment_NullRequestIds() {
            // Act & Assert
            assertThrows(
                    IllegalArgumentException.class,
                    () -> service.returnEquipment(null)
            );

            verify(requestRepository, never()).findAllById(any());
            verify(requestRepository, never()).saveAll(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when request ids list is empty")
        void returnEquipment_EmptyRequestIds() {
            // Act & Assert
            assertThrows(
                    IllegalArgumentException.class,
                    () -> service.returnEquipment(Collections.emptyList())
            );

            verify(requestRepository, never()).findAllById(any());
            verify(requestRepository, never()).saveAll(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Delete request test")
    class DeleteRequestsTest {
        @Test
        @DisplayName("Should throw IllegalArgumentException when deleteRequestsByIds is called with an empty list of requestIds")
        void deleteRequestsByIds_shouldThrowIllegalArgumentException_whenCalledWithEmptyList() {
            // Arrange
            List<Long> emptyRequestIds = Collections.emptyList();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> service.deleteRequestsByIds(emptyRequestIds));
            verify(requestRepository, never()).findAllById(any());
            verify(borrowRequestDetailRepository, never()).deleteAll(any());
            verify(logRepository, never()).deleteByEquipmentRequestIds(any());
            verify(requestRepository, never()).deleteAllInBatch(any());
        }
        @Test
        void deleteRequestsByIds_shouldRollbackAndThrowException_whenErrorOccursDuringDeletion() {
            // Arrange
            List<Long> requestIds = Arrays.asList(1L, 2L);
            List<EquipmentBorrowRequest> requests = Arrays.asList(
                    createMockRequest(1L, EquipmentBorrowRequest.Status.NOT_BORROWED),
                    createMockRequest(2L, EquipmentBorrowRequest.Status.NOT_BORROWED)
            );

            when(requestRepository.findAllById(requestIds)).thenReturn(requests);
            doThrow(new RuntimeException("Simulated error")).when(logRepository).deleteByEquipmentRequestIds(requestIds);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> service.deleteRequestsByIds(requestIds));

            // Verify that rollback operations were performed
            verify(borrowRequestDetailRepository, times(2)).saveAll(any());
            verify(requestRepository, never()).deleteAllInBatch(any());
        }

        private EquipmentBorrowRequest createMockRequest(Long id, EquipmentBorrowRequest.Status status) {
            EquipmentBorrowRequest request = mock(EquipmentBorrowRequest.class);
            when(request.getUniqueID()).thenReturn(id);
            when(request.getStatus()).thenReturn(status);
            when(request.getBorrowRequestDetails()).thenReturn(new ArrayList<>());
            return request;
        }

        @Test
        @DisplayName("Should verify that associated borrow request details are deleted when deleteRequestsByIds is called")
        void deleteRequestsByIds_shouldDeleteAssociatedBorrowRequestDetails() {
            // Arrange
            Long requestId1 = 1L;
            Long requestId2 = 2L;
            List<Long> requestIds = Arrays.asList(requestId1, requestId2);

            EquipmentBorrowRequest request1 = new EquipmentBorrowRequest();
            request1.setUniqueID(requestId1);
            request1.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            EquipmentBorrowRequestDetail detail1 = new EquipmentBorrowRequestDetail();
            request1.setBorrowRequestDetails(new ArrayList<>(Collections.singletonList(detail1)));

            EquipmentBorrowRequest request2 = new EquipmentBorrowRequest();
            request2.setUniqueID(requestId2);
            request2.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            EquipmentBorrowRequestDetail detail2 = new EquipmentBorrowRequestDetail();
            request2.setBorrowRequestDetails(new ArrayList<>(Collections.singletonList(detail2)));

            List<EquipmentBorrowRequest> requestsToDelete = Arrays.asList(request1, request2);

            when(requestRepository.findAllById(requestIds)).thenReturn(requestsToDelete);

            // Act
            service.deleteRequestsByIds(requestIds);

            // Assert
            verify(borrowRequestDetailRepository, times(2)).deleteAll(request1.getBorrowRequestDetails());
            verify(borrowRequestDetailRepository, times(2)).deleteAll(request2.getBorrowRequestDetails());
            assertTrue(request1.getBorrowRequestDetails().isEmpty());
            assertTrue(request2.getBorrowRequestDetails().isEmpty());
            verify(logRepository, times(1)).deleteByEquipmentRequestIds(requestIds);
            verify(requestRepository, times(1)).deleteAllInBatch(requestsToDelete);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when attempting to delete a request that is not in NOT_BORROWED state")
        void deleteRequestsByIds_shouldThrowIllegalStateException_whenRequestNotInNotBorrowedState() {
            // Arrange
            Long requestId = 1L;
            EquipmentBorrowRequest request = new EquipmentBorrowRequest();
            request.setUniqueID(requestId);
            request.setStatus(EquipmentBorrowRequest.Status.BORROWED);

            when(requestRepository.findAllById(Collections.singletonList(requestId)))
                    .thenReturn(Collections.singletonList(request));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> service.deleteRequestsByIds(Collections.singletonList(requestId)));

            verify(borrowRequestDetailRepository, never()).deleteAll(anyList());
            verify(logRepository, never()).deleteByEquipmentRequestIds(anyList());
            verify(requestRepository, never()).deleteAllInBatch(anyList());
        }
    }

    @Nested
    @DisplayName("Update request test")
    class UpdateRequestTest {
        @Test
        @DisplayName("Should throw IllegalStateException when attempting to update a request that is more than one day old")
        void updateRequest_ThrowsIllegalStateException_WhenRequestIsOverOneDay() {
            // Arrange
            Long requestId = 1L;
            EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
            requestDto.setUniqueID(requestId);

            EquipmentBorrowRequest existingRequest = new EquipmentBorrowRequest();
            existingRequest.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            existingRequest.setCreateAt(LocalDateTime.now().minusDays(2)); // Set creation date to 2 days ago

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> service.updateRequest(requestDto),
                    "Borrow request is overdue to update.");

            // Verify
            verify(requestRepository, times(1)).findById(requestId);
            verify(requestRepository, never()).save(any(EquipmentBorrowRequest.class));
        }

        @Test
        @DisplayName("Should handle updating a request with no changes to equipment items")
        void updateRequestWithNoChangesToEquipmentItems() {
            // Arrange
            Long requestId = 1L;
            EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
            requestDto.setUniqueID(requestId);
            requestDto.setExpectedReturnDate(LocalDate.now().plusDays(7));
            requestDto.setComment("Updated comment");
            requestDto.setEquipmentItems(Collections.emptyList());

            EquipmentBorrowRequest existingRequest = new EquipmentBorrowRequest();
            existingRequest.setUniqueID(requestId);
            existingRequest.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            existingRequest.setCreateAt(LocalDate.now().atStartOfDay());
            existingRequest.setBorrowRequestDetails(new ArrayList<>());

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
            when(requestRepository.save(any(EquipmentBorrowRequest.class))).thenReturn(existingRequest);

            // Act
            service.updateRequest(requestDto);

            // Assert
            verify(requestRepository).findById(requestId);
            verify(requestRepository).save(existingRequest);
            assertEquals(requestDto.getExpectedReturnDate(), existingRequest.getExpectedReturnDate());
            assertEquals(requestDto.getComment(), existingRequest.getComment());
            assertTrue(existingRequest.getBorrowRequestDetails().isEmpty());
        }

        @Test
        @DisplayName("Should verify that equipment quantities are properly updated in the database after request update")
        void updateRequest_shouldUpdateEquipmentQuantitiesInDatabase() {
            // Arrange
            Long requestId = 1L;
            EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
            requestDto.setUniqueID(requestId);
            requestDto.setExpectedReturnDate(LocalDate.now().plusDays(7));
            requestDto.setComment("Updated comment");

            EquipmentBorrowItemDTO item1 = new EquipmentBorrowItemDTO("Laptop", 3, null, "Good");
            EquipmentBorrowItemDTO item2 = new EquipmentBorrowItemDTO("Projector", 1, null, "Excellent");
            requestDto.setEquipmentItems(Arrays.asList(item1, item2));

            EquipmentBorrowRequest existingRequest = new EquipmentBorrowRequest();
            existingRequest.setUniqueID(requestId);
            existingRequest.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            existingRequest.setCreateAt(LocalDate.now().atStartOfDay());

            Equipment laptop = new Equipment();
            laptop.setEquipmentName("Laptop");
            Equipment projector = new Equipment();
            projector.setEquipmentName("Projector");

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
            when(equipmentRepository.findByEquipmentName("Laptop")).thenReturn(Optional.of(laptop));
            when(equipmentRepository.findByEquipmentName("Projector")).thenReturn(Optional.of(projector));
            when(detailMapper.toEntity(any(EquipmentBorrowItemDTO.class)))
                    .thenAnswer(invocation -> {
                        EquipmentBorrowItemDTO dto = invocation.getArgument(0);
                        EquipmentBorrowRequestDetail detail = new EquipmentBorrowRequestDetail();
                        detail.setQuantityBorrowed(dto.getQuantityBorrowed());
                        Equipment equipment = new Equipment();
                        equipment.setEquipmentName(dto.getEquipmentName());
                        detail.setEquipment(equipment);
                        return detail;
                    });


            // Act
            service.updateRequest(requestDto);

            // Assert
            verify(requestRepository).save(argThat(savedRequest -> {
                List<EquipmentBorrowRequestDetail> details = savedRequest.getBorrowRequestDetails();

                // Kiểm tra số lượng chi tiết
                if (details.size() != 2) return false;

                // Kiểm tra từng chi tiết có thiết bị và số lượng đúng
                boolean hasLaptop = details.stream().anyMatch(d ->
                        d.getEquipment().getEquipmentName().equals("Laptop") && d.getQuantityBorrowed() == 3);
                boolean hasProjector = details.stream().anyMatch(d ->
                        d.getEquipment().getEquipmentName().equals("Projector") && d.getQuantityBorrowed() == 1);

                // Kiểm tra nếu tất cả điều kiện đều đúng
                return hasLaptop && hasProjector;
            }));

        }
    }

    @Nested
    @DisplayName("Deny request test")
    class DenyRequestTest {
        @Test
        @DisplayName("Should throw ResourceConflictException when attempting to deny a request that is not in NOT_BORROWED status")
        void denyRequest_throwsResourceConflictException_whenRequestNotInNotBorrowedStatus() {
            // Arrange
            Long requestId = 1L;
            EquipmentBorrowRequestDenyDto denyDto = new EquipmentBorrowRequestDenyDto(requestId, "Denial reason");
            EquipmentBorrowRequest request = new EquipmentBorrowRequest();
            request.setStatus(EquipmentBorrowRequest.Status.BORROWED);

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

            // Act & Assert
            assertThrows(ResourceConflictException.class, () -> service.denyRequest(denyDto));
            verify(requestRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should successfully update the request status to REJECTED when denying a valid request")
        void denyValidRequest() {
            // Arrange
            Long requestId = 1L;
            String denialReason = "Equipment unavailable";
            EquipmentBorrowRequestDenyDto denyDto = new EquipmentBorrowRequestDenyDto(requestId, denialReason);

            EquipmentBorrowRequest request = new EquipmentBorrowRequest();
            request.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
            User user = new User();
            user.setId(2L);
            request.setUser(user);

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
            when(requestRepository.save(any(EquipmentBorrowRequest.class))).thenReturn(request);

            // Act
            service.denyRequest(denyDto);

            // Assert
            verify(requestRepository).findById(requestId);
            verify(requestRepository).save(request);
            assertEquals(EquipmentBorrowRequest.Status.REJECTED, request.getStatus());
            verify(eventPublisher).publishEvent(any(EquipmentRequestDeniedEvent.class));
        }

        @Test
        @DisplayName("Should verify that the EquipmentRequestDeniedEvent is published with correct parameters after denying a request")
        void denyRequest_shouldPublishEquipmentRequestDeniedEvent() {
            // Arrange
            Long requestId = 1L;
            String reason = "Test reason";
            EquipmentBorrowRequestDenyDto denyDto = new EquipmentBorrowRequestDenyDto(requestId, reason);

            EquipmentBorrowRequest mockRequest = mock(EquipmentBorrowRequest.class);
            User mockUser = mock(User.class);
            when(mockRequest.getStatus()).thenReturn(EquipmentBorrowRequest.Status.NOT_BORROWED);
            when(mockRequest.getUser()).thenReturn(mockUser);
            when(mockUser.getId()).thenReturn(2L);
            when(mockRequest.getUniqueID()).thenReturn(requestId);

            when(requestRepository.findById(requestId)).thenReturn(Optional.of(mockRequest));

            // Act
            service.denyRequest(denyDto);

            // Assert
            verify(requestRepository).save(mockRequest);
            verify(mockRequest).setStatus(EquipmentBorrowRequest.Status.REJECTED);
            verify(eventPublisher).publishEvent(argThat(event ->
                    event instanceof EquipmentRequestDeniedEvent &&
                            ((EquipmentRequestDeniedEvent) event).getRequestId().equals(requestId) &&
                            ((EquipmentRequestDeniedEvent) event).getUserId().equals(2L) &&
                            ((EquipmentRequestDeniedEvent) event).getReason().equals(reason)
            ));
        }
    }

}