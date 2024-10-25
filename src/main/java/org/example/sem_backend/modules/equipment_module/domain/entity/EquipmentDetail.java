package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.entity.Room;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EquipmentDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String purchaseDate;
    private String description;
    private String code;
    private String currentStatus;
    private int operatingHours = 0;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne(cascade = CascadeType.ALL    )
    @JoinColumn(name = "room_id")
    private Room room;

}
