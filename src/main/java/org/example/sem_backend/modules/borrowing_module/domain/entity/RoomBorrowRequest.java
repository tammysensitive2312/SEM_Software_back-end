package org.example.sem_backend.modules.borrowing_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.user_module.domain.entity.User;

@Entity
@Table(name = "room_borrow_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomBorrowRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
