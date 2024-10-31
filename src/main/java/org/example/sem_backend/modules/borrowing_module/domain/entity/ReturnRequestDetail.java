package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;

@Entity
@Table(name = "return_request_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnRequestDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    @ManyToOne
    @JoinColumn(name = "return_id", nullable = false)
    private ReturnRequest returnRequest;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentDetail equipment;

    @Column(name = "quantity_returned", nullable = false)
    private int quantityReturned;

    @Column(name = "condition_after_return", length = 50)
    private String conditionAfterReturn;
}
