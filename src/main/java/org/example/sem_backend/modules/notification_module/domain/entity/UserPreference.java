package org.example.sem_backend.modules.notification_module.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.modules.user_module.domain.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean emailNotification;
    private boolean inAppNotification;
    private boolean pushNotification;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
