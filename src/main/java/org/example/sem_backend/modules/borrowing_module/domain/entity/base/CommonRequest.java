package org.example.sem_backend.modules.borrowing_module.domain.entity.base;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.common_module.entity.BaseEntity;

@MappedSuperclass
@RequiredArgsConstructor
@Getter
@Setter
public abstract class CommonRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueID;
    @Column(columnDefinition = "TEXT")
    private String comment;
}
