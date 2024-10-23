package org.example.sem_backend.modules.room_module.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private int capacity = 0;
}
