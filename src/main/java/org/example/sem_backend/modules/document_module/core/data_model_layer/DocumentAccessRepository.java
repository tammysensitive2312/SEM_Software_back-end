package org.example.sem_backend.modules.document_module.core.data_model_layer;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.document_module.core.data_access_layer.DocumentRepository;
import org.example.sem_backend.modules.document_module.core.data_access_layer.DocumentVersionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentAccessRepository {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;

    public Document saveDocumentWithRevision(Document document) {
        return documentRepository.save(document);
    }
}
