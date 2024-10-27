package org.example.sem_backend.common_module.common;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.example.sem_backend.common_module.entity.BaseEntity;

import java.time.LocalDateTime;

public class AuditEntityListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
        entity.setCreateAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
    }
    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
