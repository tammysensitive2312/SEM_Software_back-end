package org.example.sem_backend.modules.document_module.core.application_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.example.sem_backend.modules.document_module.core.ProgressTrackingInputStream;
import org.example.sem_backend.modules.document_module.core.UploadProgressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class HdfsFileService {
    private final FileSystem hdfsFileSystem;
    private final UploadProgressService progressService;
    private final Executor hdfsExecutor;
    @Value("${upload.default.directory}")
    private String defaultDirectory;

    public String uploadFileAsync(
            MultipartFile file
    ) {
        String fileId = UUID.randomUUID().toString();
        String hdfsPath = generateHdfsPath(file);
        log.info("Uploading file {} to HDFS path {}", fileId, hdfsPath);

        // Khởi tạo progress tracking
        progressService.initializeProgress(
                fileId,
                file.getOriginalFilename(),
                file.getSize()
        );

        try {
            return uploadFileWithProgress(file, hdfsPath, fileId);
        } catch (Exception e) {
            progressService.markFailed(fileId, e.getMessage());
            throw new CompletionException(e);
        }
    }

    private String uploadFileWithProgress(MultipartFile file, String hdfsPath, String fileId)
            throws IOException {
        org.apache.hadoop.fs.Path hdfsFilePath = new org.apache.hadoop.fs.Path(
                hdfsPath + "/" + sanitizeFileName(Objects.requireNonNull(file.getOriginalFilename()))
        );
        org.apache.hadoop.fs.Path hdfsDirectory = new org.apache.hadoop.fs.Path(hdfsPath);

        String tempDirectory = defaultDirectory;
        Path tempDirPath = Paths.get(tempDirectory);
        if (!Files.exists(tempDirPath)) {
            Files.createDirectories(tempDirPath);
        }
        Path tempFilePath = Paths.get(tempDirectory, fileId + "_" + file.getOriginalFilename());
        try {
            file.transferTo(tempFilePath);  // Lưu file vào thư mục tạm

            if (!hdfsFileSystem.exists(hdfsDirectory)) {
                hdfsFileSystem.mkdirs(hdfsDirectory);
            }

            try (InputStream inputStream = new ProgressTrackingInputStream(
                    Files.newInputStream(tempFilePath), progressService, fileId);
                 OutputStream outputStream = hdfsFileSystem.create(hdfsFilePath, true)) {

                byte[] buffer = new byte[8192];  // Buffer size
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();

                progressService.markCompleted(fileId);
                return hdfsFilePath.toString();
            }
        } catch (Exception e) {
            log.error("Error uploading file to HDFS", e);
            progressService.markFailed(fileId, e.getMessage());
            throw e;
        } finally {
            Files.deleteIfExists(tempFilePath);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String generateHdfsPath(MultipartFile file) {
        return String.format("/data-lake/%s/%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                file.getName());
    }
}
