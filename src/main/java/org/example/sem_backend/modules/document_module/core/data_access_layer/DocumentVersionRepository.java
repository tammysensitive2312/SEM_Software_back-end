package org.example.sem_backend.modules.document_module.core.data_access_layer;

import org.example.sem_backend.modules.document_module.core.data_model_layer.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
}