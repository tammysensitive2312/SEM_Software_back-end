package org.example.sem_backend.modules.document_module.core.service;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

// Factory class
public class DocumentParserFactory {
    private static final Tika tika = new Tika();

    public static DocumentParser getParser(String filePath) throws IOException {
        File file = new File(filePath);
        String mimeType = tika.detect(file);

        return switch (mimeType) {
            case "application/pdf" -> new PdfFileParser(filePath);
//            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
//                    new DocxDocumentParser(filePath);
//            case "application/msword" -> new DocDocumentParser(filePath);
//            case "text/plain" -> new TxtDocumentParser(filePath);
            default -> throw new UnsupportedOperationException(
                    "Unsupported document type: " + mimeType);
        };
    }
}
