package org.example.sem_backend.modules.room_module.domain.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private int id;
    // một dto handle cho cả request và response
    @NotBlank(message = "Tên phòng không được để trống")
    @Column(unique = true)
    private String roomName;
    private String type;
    private int capacity;
    private RoomStatus status;
}
