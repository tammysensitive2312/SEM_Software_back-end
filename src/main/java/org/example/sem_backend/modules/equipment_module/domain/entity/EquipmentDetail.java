package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.room_module.domain.entity.Room;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "equipment_details")
public class EquipmentDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ngày mua không được để trống")
    private String purchaseDate;

    private String description;

    @NotBlank(message = "mã thiết bị không được để trống")
    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private EquipmentDetailStatus status;

    private int operatingHours;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
