package org.example.sem_backend.modules.room_module.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;

import java.util.HashSet;
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
    private Integer id;

    @NotBlank(message = "Tên phòng không được để trống")
    @Column(unique = true)
    private String roomName;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private int capacity;
}
