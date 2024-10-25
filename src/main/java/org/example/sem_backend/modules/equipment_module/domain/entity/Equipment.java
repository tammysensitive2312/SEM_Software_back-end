package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.example.sem_backend.common_module.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Persistent;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "tên thiết bị không được để trống")
    @Column(unique = true)
    private String equipmentName;

    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentDetail> equipmentDetails;
}
