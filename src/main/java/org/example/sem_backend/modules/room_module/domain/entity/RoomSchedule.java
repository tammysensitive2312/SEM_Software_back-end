package org.example.sem_backend.modules.room_module.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String startTime;
    private String endTime;
    private String user;
}
