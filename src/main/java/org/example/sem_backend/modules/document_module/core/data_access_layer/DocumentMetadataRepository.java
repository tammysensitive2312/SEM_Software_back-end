package org.example.sem_backend.modules.document_module.core.data_access_layer;

import org.example.sem_backend.modules.document_module.core.storage_layer.DocumentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentMetadataRepository extends MongoRepository<DocumentMetadata, Long> {
}
