package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.sem_backend.modules.equipment_module.enums.Category;

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
    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentDetail> equipmentDetails;
}
