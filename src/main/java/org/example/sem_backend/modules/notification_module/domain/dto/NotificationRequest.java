package org.example.sem_backend.modules.notification_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;

import java.io.Serializable;

/**
 * DTO for {@link Notification}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationRequest implements Serializable {
    String message;
}