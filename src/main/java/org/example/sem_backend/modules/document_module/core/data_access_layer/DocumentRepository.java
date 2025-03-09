package org.example.sem_backend.modules.document_module.core.data_access_layer;

import org.example.sem_backend.modules.document_module.core.data_model_layer.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Document findByFileId(String fileId);
}