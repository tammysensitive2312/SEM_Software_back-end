package org.example.sem_backend.modules.document_module.core.application_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.application_layer.file_system_helper.UploadProgressDTO;
import org.example.sem_backend.modules.document_module.core.application_layer.file_system_helper.UploadProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
@Slf4j
public class DocumentManagementController {
    private final DocumentManagementService documentManagementService;
    private final UploadProgressService progressService;

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

    @GetMapping("/progress/{fileId}")
    public ResponseEntity<UploadProgressDTO> getProgress(
            @PathVariable String fileId) {
        UploadProgressDTO progress = progressService.getProgress(fileId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }
}
