package org.example.sem_backend.common_module.service.schedule_service;

import java.time.LocalDateTime;

public interface ScheduledTask {
    void execute();
    LocalDateTime getExecutionTime();
}
