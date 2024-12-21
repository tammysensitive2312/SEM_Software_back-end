package org.example.sem_backend.common_module.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GenericEvent<T> extends ApplicationEvent {
    private final T data;

    public GenericEvent(Object source, T data) {
        super(source);
        this.data = data;
    }

}
