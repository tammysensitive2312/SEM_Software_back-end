package org.example.sem_backend.modules.document_module.core.data_access_layer;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.document_module.core.storage_layer.DocumentMetadata;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final DocumentMetadataRepository documentMetadataRepository;

    public void saveMetadata(DocumentMetadata documentMetadata) {
        documentMetadataRepository.save(documentMetadata);
    }

    public DocumentMetadata getMetadata(Long id) {
        return documentMetadataRepository.findById(id).orElse(null);
    }
}
