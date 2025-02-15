package org.example.sem_backend.modules.document_module.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.service.DocumentManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
@Slf4j
public class DocumentManagementController {
    private final DocumentManagementService documentManagementService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocuments(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select at least one file to upload");
            }

            List<String> uploadedFiles = documentManagementService.uploadDocuments(files);
            return ResponseEntity.ok("Successfully uploaded " + uploadedFiles.size() + " files");

        } catch (Exception e) {
            log.error("Failed to upload files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
