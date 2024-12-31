package org.example.sem_backend.modules.borrowing_module.domain.dto.room;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for retrieving room booking requests with associated schedules.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomRequestDTO {
    private Long uniqueId;
    private String roomName;
    private String username;
    private String email;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String comment;
    private boolean isCancelable;
}
