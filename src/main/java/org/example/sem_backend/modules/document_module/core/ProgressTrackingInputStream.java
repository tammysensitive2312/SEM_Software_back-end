package org.example.sem_backend.modules.document_module.core;

import java.io.IOException;
import java.io.InputStream;

public class ProgressTrackingInputStream extends InputStream {
    private final InputStream inputStream;
    private final UploadProgressService progressService;
    private final String fileId;
    private long totalBytesRead = 0;
    private boolean closed = false;

    public ProgressTrackingInputStream(
            InputStream inputStream,
            UploadProgressService progressService,
            String fileId) {
        this.inputStream = inputStream;
        this.progressService = progressService;
        this.fileId = fileId;
    }

    @Override
    public int read() throws IOException {
        int byteRead = inputStream.read();
        if (byteRead != -1) {
            totalBytesRead++;
            progressService.updateProgress(fileId, totalBytesRead);
        }
        return byteRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = inputStream.read(b, off, len);
        if (bytesRead != -1) {
            totalBytesRead += bytesRead;
            progressService.updateProgress(fileId, totalBytesRead);
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            inputStream.close();
        }
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }
}
