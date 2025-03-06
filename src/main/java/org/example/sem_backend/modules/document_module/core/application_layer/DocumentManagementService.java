package org.example.sem_backend.modules.document_module.core.application_layer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.modules.document_module.core.parser_layer.DocumentParser;
import org.example.sem_backend.modules.document_module.core.parser_layer.DocumentParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentManagementService {
    private final LocalFileService localFileService;

    public List<String> uploadDocuments(List<MultipartFile> files) {
        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> localFileService.uploadFileAsync(file)
                        .whenComplete((path, ex) -> {
                            if (ex == null) {
                                try {
                                    processDocument(path);
                                } catch (Exception processEx) {
                                    log.error("Error processing document: {}", path, processEx);
                                }
                            }
                        }))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public void processDocument(String path) {
        File source = new File(path);
        if (source.isDirectory()) {
            log.info("Processing directory: {}", source.getName());
            processDirectory(source);
        } else {
            log.info("Processing file: {}", source.getName());
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
            log.info("Processing file path: {}", file.getPath());
            DocumentParser parser = DocumentParserFactory.getParser(file.getPath());
            parser.parseDocument(file.getPath());
        } catch (UnsupportedOperationException e) {
            log.debug("Skipping unsupported file: {}", file.getName());
        } catch (IOException e) {
            log.debug("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }
}
