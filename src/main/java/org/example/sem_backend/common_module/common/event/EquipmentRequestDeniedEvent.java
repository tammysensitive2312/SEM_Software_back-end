package org.example.sem_backend.common_module.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EquipmentRequestDeniedEvent extends ApplicationEvent {
    private final Long requestId;
    private final Long userId;
    private final String reason;

    public EquipmentRequestDeniedEvent(Object source, Long requestId, Long userId, String reason) {
        super(source);
        this.requestId = requestId;
        this.userId = userId;
        this.reason = reason;
    }
}
