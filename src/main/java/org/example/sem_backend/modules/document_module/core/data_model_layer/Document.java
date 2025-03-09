package org.example.sem_backend.modules.document_module.core.data_model_layer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sem_backend.common_module.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "documents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileId;
    private String filePath;
    private String fileName;
    private Long fileSize;
    @Column(columnDefinition = "JSON")
    private String metadata;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentVersion> versions = new ArrayList<>();
}
