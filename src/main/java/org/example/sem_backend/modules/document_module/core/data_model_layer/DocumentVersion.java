package org.example.sem_backend.modules.document_module.core.data_model_layer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.common_module.entity.BaseEntity;

@Entity(name = "document_versions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentVersion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String revisionId;
    private Long size;
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
}
