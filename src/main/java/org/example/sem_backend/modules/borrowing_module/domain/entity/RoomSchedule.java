package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.modules.room_module.domain.entity.Room;

import java.time.LocalDateTime;

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

    @OneToOne
    @JoinColumn(name = "request_id", unique = true)
    private RoomBorrowRequest request;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String user;

    @Version
    private Integer version;

}
