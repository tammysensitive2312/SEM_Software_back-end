package org.example.sem_backend.modules.borrowing_module.domain.dto.room;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomRequestDTO {
    private Long uniqueId;
    private String roomName;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
