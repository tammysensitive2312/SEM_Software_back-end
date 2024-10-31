package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.enums.Category;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "equipments")
@Builder
public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentDetail> equipmentDetails;
}
