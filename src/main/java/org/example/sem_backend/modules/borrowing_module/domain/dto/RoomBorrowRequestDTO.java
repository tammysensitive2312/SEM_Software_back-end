package org.example.sem_backend.modules.borrowing_module.domain.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
RoomBorrowRequestDTO {
    @NotNull(message = "Unique ID cần cho cập nhật")
    private Long uniqueId;

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Room ID không được để trống")
    private Long roomId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải ở tương lai")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Future(message = "Thời gian kết thúc phải ở tương lai")
    private LocalDateTime endTime;

    private String comment;
}
