package org.example.sem_backend.modules.room_module.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomStatus {
    AVAILABLE("Đang trống"),
    OCCUPIED("Đang sử dụng"),
    BROKEN("Hỏng");

    private final String value;
}
