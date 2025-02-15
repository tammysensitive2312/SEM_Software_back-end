package org.example.sem_backend.modules.document_module.core.storage_layer;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {
    @Id
    private Long id;

    private String title;
    private String author;
    private LocalDateTime uploadDate;
    private String summary;
    private String hash;

    @Field("additional_fields")
    private HashMap<String, Object> additionalFields;
}
