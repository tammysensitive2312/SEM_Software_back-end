package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment_borrow_request_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EquipmentBorrowRequestDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    @ManyToOne
    @JoinColumn(name = "borrow_request_id", nullable = false)
    @ToString.Exclude
    private EquipmentBorrowRequest borrowRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToMany
    @JoinTable(
            name = "borrow_detail_equipment",
            joinColumns = @JoinColumn(name = "borrow_request_detail_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_detail_id")
    )
    private List<EquipmentDetail> equipmentDetails = new ArrayList<>();

    @Column(name = "quantity_borrowed", nullable = false)
    private int quantityBorrowed;

    @Column(name = "condition_before_borrow", length = 50)
    private String conditionBeforeBorrow;
}
