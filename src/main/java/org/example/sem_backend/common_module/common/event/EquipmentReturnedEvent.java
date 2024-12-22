package org.example.sem_backend.common_module.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class EquipmentReturnedEvent extends ApplicationEvent {
    private final List<Long> requestId;

    public EquipmentReturnedEvent(Object source, List<Long> requestId) {
        super(source);
        this.requestId = requestId;
    }
}
