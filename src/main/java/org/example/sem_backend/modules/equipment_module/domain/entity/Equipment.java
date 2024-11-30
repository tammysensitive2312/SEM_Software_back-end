package org.example.sem_backend.modules.equipment_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.enums.Category;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "equipments")
public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String equipmentName;

    @Column(unique = true, nullable = false)
    private String code;

    private int totalQuantity;
    private int usableQuantity;
    private int brokenQuantity;
    private int inUseQuantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentDetail> equipmentDetails;

    @Version
    private int version;
}
