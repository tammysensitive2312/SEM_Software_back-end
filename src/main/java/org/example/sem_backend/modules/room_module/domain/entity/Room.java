package org.example.sem_backend.modules.room_module.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.sem_backend.modules.room_module.enums.RoomCondition;
import org.example.sem_backend.modules.room_module.enums.RoomType;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    private String number;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private int capacity;
    @Enumerated(EnumType.STRING)
    private RoomCondition roomCondition;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomSchedule> roomSchedules;
}
