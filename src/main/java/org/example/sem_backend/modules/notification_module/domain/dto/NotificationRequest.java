package org.example.sem_backend.modules.notification_module.domain.dto;

import lombok.Getter;
import lombok.Value;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;

import java.io.Serializable;

/**
 * DTO for {@link Notification}
 */
@Value
@Getter
public class NotificationRequest implements Serializable {
    String message;
    boolean isRead;
}