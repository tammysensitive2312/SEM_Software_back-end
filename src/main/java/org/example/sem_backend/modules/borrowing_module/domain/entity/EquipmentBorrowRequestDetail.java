package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;

@Entity
@Table(name = "equipment_borrow_request_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EquipmentBorrowRequestDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;
    @ManyToOne
    @JoinColumn(name = "borrow_request_id", nullable = false)
    private EquipmentBorrowRequest borrowRequest;
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentDetail equipment;
    @Column(name = "quantity_borrowed", nullable = false)
    private int quantityBorrowed;
    @Column(name = "condition_before_borrow", length = 50)
    private String conditionBeforeBorrow;
}
