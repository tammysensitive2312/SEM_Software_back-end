package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.user_module.domain.entity.User;

import java.time.LocalDate;


@Entity
@Table(name = "equipment_borrow_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EquipmentBorrowRequest extends BaseEntity {

    public enum Status {
        NOT_BORROWED, BORROWED, RETURNED, PARTIALLY_RETURNED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('Not Borrowed', 'Borrowed', 'Returned', 'Partially Returned') DEFAULT 'Not Borrowed'")
    private Status status = Status.NOT_BORROWED;
    @Column(columnDefinition = "TEXT")
    private String comment;
}
