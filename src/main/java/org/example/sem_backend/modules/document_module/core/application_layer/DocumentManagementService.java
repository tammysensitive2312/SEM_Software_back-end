package org.example.sem_backend.modules.document_module.core.application_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.parser_layer.DocumentParser;
import org.example.sem_backend.modules.document_module.core.parser_layer.DocumentParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentManagementService {
    private final HdfsFileService hdfsFileService;

    public List<String> uploadDocuments(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            String path = uploadSingleDocument(file);
            paths.add(path);
        }
        return paths;
    }

    private String uploadSingleDocument(MultipartFile file) {
        String filePath = String.valueOf(hdfsFileService.uploadFileAsync(file));
        return filePath;
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
