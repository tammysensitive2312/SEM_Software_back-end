package org.example.sem_backend.common_module.service.schedule_service;

import lombok.Getter;
import lombok.Setter;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.service.RoomService;
import org.slf4j.Logger;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoomStatusChangeTask extends AbstractTask {
    private Long roomId;
    private RoomStatus newStatus;
    private RoomService roomService;

    public RoomStatusChangeTask(LocalDateTime executionTime, Logger logger) {
        super(executionTime, logger);
    }

    @Override
    public void execute() {
        getLogger().info(
                "Executing task for Room ID: {}, changing status to: {}, at: {}, Thread: {}",
                roomId, newStatus, getExecutionTime(), Thread.currentThread().getName()
        );
        roomService.changeRoomStatus(newStatus, roomId);
    }
}
