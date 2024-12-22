package org.example.sem_backend.common_module.exception;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {
    private final String errorType;

    public InvalidCredentialsException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

}
