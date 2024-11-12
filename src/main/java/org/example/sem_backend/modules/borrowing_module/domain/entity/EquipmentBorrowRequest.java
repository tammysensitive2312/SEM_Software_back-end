package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.modules.borrowing_module.domain.entity.base.CommonRequest;
import org.example.sem_backend.modules.user_module.domain.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "equipment_borrow_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EquipmentBorrowRequest extends CommonRequest {
    public enum Status {
        NOT_BORROWED, BORROWED, RETURNED, PARTIALLY_RETURNED
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(mappedBy = "borrowRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipmentBorrowRequestDetail> borrowRequestDetails = new ArrayList<>();
}
