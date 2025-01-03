package org.example.sem_backend.modules.user_module.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.sem_backend.common_module.entity.BaseEntity;

@Entity(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
//    @ValidPassword
    private String password;

    @Enumerated(EnumType.STRING)
    private ERole role;
}
