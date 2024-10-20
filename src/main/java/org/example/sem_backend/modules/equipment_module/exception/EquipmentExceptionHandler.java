package org.example.sem_backend.modules.equipment_module.exception;

import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class serves as a global exception handler for the Equipment Module.
 * It catches and handles exceptions thrown by the Equipment Module's controllers.
 *
 * @RestControllerAdvice(basePackages = "org.example.sem_backend.modules.equipment_module")
 */
@RestControllerAdvice(basePackages = "org.example.sem_backend.modules.equipment_module")
public class EquipmentExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(404), "Thiết bị không được tìm thấy.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }
}
