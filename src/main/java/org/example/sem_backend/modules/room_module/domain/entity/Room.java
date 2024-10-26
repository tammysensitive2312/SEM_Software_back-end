package org.example.sem_backend.modules.room_module.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.enums.RoomType;

import java.util.Set;

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

    private String description;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private int capacity;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomSchedule> roomSchedules;
}
