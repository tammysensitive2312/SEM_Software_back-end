package org.example.sem_backend.common_module.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.example.sem_backend.common_module.common.AuditEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class BaseEntity {
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class BaseEntityWithDelete extends BaseEntity {
    int deleteAt;
}
