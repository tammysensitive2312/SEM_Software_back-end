package org.example.sem_backend.modules.equipment_module.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.room_module.domain.entity.Room;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "equipment_details")
@Builder
public class EquipmentDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;

    private String description;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private EquipmentDetailStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

}
