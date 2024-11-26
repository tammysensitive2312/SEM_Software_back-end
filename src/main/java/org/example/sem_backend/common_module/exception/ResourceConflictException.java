package org.example.sem_backend.common_module.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
    private final String module; // Thêm trường module để xác định nguồn gốc của ngoại lệ

    public ResourceConflictException(String message, String module) {
        super(message);
        this.module = module;
    }

    public ResourceConflictException(String message, Throwable cause, String module) {
        super(message, cause);
        this.module = module;
    }
}
