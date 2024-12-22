package org.example.sem_backend.common_module.exception;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String email) {
        super("Email " + email + " already exists");
    }
}
