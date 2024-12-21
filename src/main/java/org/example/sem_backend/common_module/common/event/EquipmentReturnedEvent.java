package org.example.sem_backend.common_module.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EquipmentReturnedEvent extends ApplicationEvent {
    private final Long requestId;
    private final Long userId;

    public EquipmentReturnedEvent(Object source, Long requestId, Long userId) {
        super(source);
        this.requestId = requestId;
        this.userId = userId;
    }
}
