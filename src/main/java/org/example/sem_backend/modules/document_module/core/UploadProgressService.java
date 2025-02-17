package org.example.sem_backend.modules.document_module.core;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadProgressService {
    private final ConcurrentHashMap<String, UploadProgressDTO> progressMap = new ConcurrentHashMap<>();

    public void initializeProgress(String fileId, String fileName, long totalBytes) {
        UploadProgressDTO progress = new UploadProgressDTO(
                fileId, fileName, totalBytes, 0L, 0, ProgressEnum.PENDING, null
        );
        progressMap.put(fileId, progress);
    }

    public void updateProgress(String fileId, long uploadedBytes) {
        UploadProgressDTO progress = progressMap.get(fileId);
        if (progress != null) {
            progress.setUploadedBytes(uploadedBytes);
            progress.setProgressPercent((int) (uploadedBytes * 100 / progress.getTotalBytes()));
            progress.setStatus(ProgressEnum.IN_PROGRESS);
        }
    }

    public void markCompleted(String fileId) {
        UploadProgressDTO progress = progressMap.get(fileId);
        if (progress != null) {
            progress.setProgressPercent(100);
            progress.setStatus(ProgressEnum.COMPLETED);
        }
    }

    public void markFailed(String fileId, String error) {
        UploadProgressDTO progress = progressMap.get(fileId);
        if (progress != null) {
            progress.setStatus(ProgressEnum.FAILED);
            progress.setErrorMessage(error);
        }
    }

    public UploadProgressDTO getProgress(String fileId) {
        return progressMap.get(fileId);
    }

    @Scheduled(fixedRate = 3600000) // Clean up every hour
    public void cleanupOldEntries() {
        progressMap.entrySet().removeIf(entry -> {
            UploadProgressDTO progress = entry.getValue();
            return isOldEntry(progress) || isCompletedOrFailed(progress);
        });
    }

    private boolean isOldEntry(UploadProgressDTO progress) {
        return progress.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24));
    }

    private boolean isCompletedOrFailed(UploadProgressDTO progress) {
        return progress.getStatus() == ProgressEnum.COMPLETED ||
                progress.getStatus() == ProgressEnum.FAILED;
    }

    public void validateFileId(String fileId) {
        if (!progressMap.containsKey(fileId)) {
            throw new IllegalArgumentException("Invalid file ID: " + fileId);
        }
    }
}
