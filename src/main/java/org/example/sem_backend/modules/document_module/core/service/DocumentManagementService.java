package org.example.sem_backend.modules.document_module.core.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.service.parser_layer.DocumentParser;
import org.example.sem_backend.modules.document_module.core.service.parser_layer.DocumentParserFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DocumentManagementService {

    @Value("${upload.default.directory}")
    private String defaultDirectory;

    public List<String> uploadDocuments(List<MultipartFile> files) throws IOException {
        List<String> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filePath = uploadSingleDocument(file);
                uploadedFiles.add(filePath);
            }
        }

        return uploadedFiles;
    }

    private String uploadSingleDocument(MultipartFile file) throws IOException {
        String absolutePath = new File("").getAbsolutePath();
        Path uploadPath = Paths.get(absolutePath + defaultDirectory);

        Files.createDirectories(uploadPath);

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

        Path targetLocation = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        processDocument(targetLocation.toString());

        log.info("File saved to: " + targetLocation);
        return targetLocation.toString();
    }

    public void processDocument(String path) {
        File source = new File(path);
        if (source.isDirectory()) {
            processDirectory(source);
        } else {
            processSingleFile(source);
        }
    }

    private void processDirectory(File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                processSingleFile(file);
            }
        }
    }

    private void processSingleFile(File file) {
        try {
            DocumentParser parser = DocumentParserFactory.getParser(file.getPath());
            parser.parseDocument(file.getPath());
        } catch (UnsupportedOperationException e) {
            log.debug("Skipping unsupported file: {}", file.getName());
        } catch (IOException e) {
            log.debug("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }
}
