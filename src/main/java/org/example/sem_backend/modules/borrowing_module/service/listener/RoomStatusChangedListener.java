package org.example.sem_backend.modules.borrowing_module.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.common.event.GenericEvent;
import org.example.sem_backend.modules.borrowing_module.repository.RoomBorrowRequestRepository;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class RoomStatusChangedListener {

    private final RoomBorrowRequestRepository borrowingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RoomStatusChangedListener(RoomBorrowRequestRepository roomBorrowingRepository, ApplicationEventPublisher publishEvent) {
        this.borrowingRepository = roomBorrowingRepository;
        this.eventPublisher = publishEvent;
    }

    @EventListener
    public void handleRoomStatusChange(GenericEvent<Long> event) {
        if (event.getSource() instanceof RoomService) {
            Long roomId = event.getData();
            if (roomId == null) {
                log.error("roomId in event context is null");
            }
            log.info("RoomStatusChangedListener: Handling room status change for roomId: {}", roomId);

            List<Long> userIds = borrowingRepository.findUserIdsWithBookingsAfter(LocalDateTime.now(), roomId);

            if (!userIds.isEmpty()) {
                log.info("Notifying users: {}", userIds);
                eventPublisher.publishEvent(new GenericEvent<>(this, userIds));
            }
        }
        log.error("Mismatch type event {}", event.getSource());
    }
}
