package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.user_module.domain.entity.User;


@Entity
@Table(name = "transactions_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionsLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_request_id")
    private RoomBorrowRequest roomRequest;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_request_id")
    private EquipmentBorrowRequest equipmentRequest;
}
