package org.example.sem_backend.modules.document_module.core;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadProgressDTO {
    private String fileId;
    private String fileName;
    private long totalBytes;
    private long uploadedBytes;
    private int progressPercent;
    private ProgressEnum status; // PENDING, IN_PROGRESS, COMPLETED, FAILED
    private String errorMessage;
    private LocalDateTime createdAt;

    public UploadProgressDTO(String fileId, String fileName, long totalBytes,
                             long uploadedBytes, int progressPercent, ProgressEnum status, String errorMessage) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.totalBytes = totalBytes;
        this.uploadedBytes = uploadedBytes;
        this.progressPercent = progressPercent;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
    }
}
