package org.example.sem_backend.modules.notification_module.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.common_module.entity.BaseEntity;
import org.example.sem_backend.modules.notification_module.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private NotificationType type;

    @ElementCollection
    @CollectionTable(name = "notification_recipients", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "recipient_id")
    private Set<Long> recipients = new HashSet<>();

    private boolean isRead = false;
    private LocalDateTime readAt;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + getId() + ", " +
                "message = " + getMessage() + ", " +
                "type = " + getType() + ", " +
                "isRead = " + isRead() + ", " +
                "readAt = " + getReadAt() + ", " +
                "createAt = " + getCreateAt() + ", " +
                "updatedAt = " + getUpdatedAt() + ")";
    }
}
