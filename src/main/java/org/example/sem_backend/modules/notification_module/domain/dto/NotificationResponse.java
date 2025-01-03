package org.example.sem_backend.modules.notification_module.domain.dto;

import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class NotificationResponse {
    String message;
    String time;
    boolean isRead;
}
