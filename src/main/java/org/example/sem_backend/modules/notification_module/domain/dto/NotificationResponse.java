package org.example.sem_backend.modules.notification_module.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@AllArgsConstructor
public class NotificationResponse {
    Long id;
    String message;
    String time;
    boolean isRead;
}
