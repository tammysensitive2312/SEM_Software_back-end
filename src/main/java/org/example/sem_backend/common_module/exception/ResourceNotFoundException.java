package org.example.sem_backend.common_module.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String module;

    public ResourceNotFoundException(String message, String module) {
        super(message);
        this.module = module;
    }

    public ResourceNotFoundException(String message, Throwable cause, String module) {
        super(message, cause);
        this.module = module;
    }
}
