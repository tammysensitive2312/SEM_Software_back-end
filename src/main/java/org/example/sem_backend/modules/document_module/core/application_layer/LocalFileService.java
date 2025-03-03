package org.example.sem_backend.modules.document_module.core.application_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.ProgressTrackingInputStream;
import org.example.sem_backend.modules.document_module.core.UploadProgressService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileService {
    private final UploadProgressService progressService;
    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;

    @Value("${upload.default.directory}")
    private String storageDirectory;

    public CompletableFuture<String> uploadFileAsync(MultipartFile file) {
        String fileId = UUID.randomUUID().toString();
        String localPath = generateLocalPath(file);
        log.info("Uploading file {} to local path {}", fileId, localPath);

        // Khởi tạo progress tracking
        progressService.initializeProgress(
                fileId,
                file.getOriginalFilename(),
                file.getSize()
        );

        return CompletableFuture.supplyAsync(() -> {
            try {
                return uploadFileWithProgress(file, localPath, fileId);
            } catch (Exception e) {
                progressService.markFailed(fileId, e.getMessage());
                throw new CompletionException(e);
            }
        }, taskExecutor);
    }

    private String uploadFileWithProgress(MultipartFile file, String localPath, String fileId)
            throws IOException {
        Path targetDirectory = Paths.get(localPath);
        Path targetFilePath = targetDirectory.resolve(
                sanitizeFileName(Objects.requireNonNull(file.getOriginalFilename()))
        );

        // Đảm bảo thư mục đích tồn tại
        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }

        // Trực tiếp đọc từ MultipartFile và ghi vào thư mục đích với theo dõi tiến trình
        try (InputStream inputStream = new ProgressTrackingInputStream(
                file.getInputStream(), progressService, fileId);
             OutputStream outputStream = Files.newOutputStream(targetFilePath)) {

            byte[] buffer = new byte[8192];  // Buffer size
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            progressService.markCompleted(fileId);
            return targetFilePath.toString();
        } catch (Exception e) {
            log.error("Error uploading file to local storage", e);
            progressService.markFailed(fileId, e.getMessage());
            throw e;
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String generateLocalPath(MultipartFile file) {
        return Paths.get(
                storageDirectory,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                file.getName()
        ).toString();
    }
}