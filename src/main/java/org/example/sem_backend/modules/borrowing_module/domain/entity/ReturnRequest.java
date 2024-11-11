package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.borrowing_module.domain.entity.base.CommonRequest;
import org.example.sem_backend.modules.user_module.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnRequest extends CommonRequest {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", length = 20)
    private String status = "chờ duyệt";

    @Column(name = "condition_after_return", length = 50)
    private String conditionAfterReturn;
}
