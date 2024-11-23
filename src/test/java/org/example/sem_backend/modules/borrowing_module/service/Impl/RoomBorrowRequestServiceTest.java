package org.example.sem_backend.modules.borrowing_module.service.Impl;

import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.borrowing_module.repository.TransactionsLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomBorrowRequestServiceTest {

    @Mock
    private RoomBorrowRequestRepository roomBorrowRequestRepository;

    @InjectMocks
    private RoomBorrowRequestService roomBorrowRequestService;

    @Mock
    private TransactionsLogRepository transactionsLogRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteRequestsByIds_ShouldDeleteLogsAndRequests_WhenValidIds() {
        // Mock dữ liệu
        RoomBorrowRequest request1 = new RoomBorrowRequest();
        request1.setUniqueID(1L);

        RoomBorrowRequest request2 = new RoomBorrowRequest();
        request2.setUniqueID(2L);

        when(roomBorrowRequestRepository.findAllById(anyList()))
                .thenReturn(List.of(request1, request2));

        // Execute
        roomBorrowRequestService.deleteRequestsByIds(List.of(1L, 2L));

        // Verify
        verify(transactionsLogRepository).deleteByRoomRequestIds(List.of(1L, 2L));
        verify(roomBorrowRequestRepository).deleteAllInBatch(List.of(request1, request2));
    }

}