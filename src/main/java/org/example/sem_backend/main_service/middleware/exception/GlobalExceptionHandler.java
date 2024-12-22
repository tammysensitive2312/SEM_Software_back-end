package org.example.sem_backend.main_service.middleware.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.example.sem_backend.common_module.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex, WebRequest request) {
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail handleResourceConflictException(ResourceConflictException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.CONFLICT, "Resource Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Bad Request", "Dữ liệu nhập không hợp lệ", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, "Constraint Violation", "Validation failure", request);

        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            violations.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Data Integrity Violation", Objects.requireNonNull(ex.getRootCause()).getMessage(), request);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ProblemDetail handleJpaSystemException(JpaSystemException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "JPA System Error", Objects.requireNonNull(ex.getRootCause()).getMessage(), request);
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failure", request);

        Map<String, String> violations = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> violations.put(fieldError.getField(), fieldError.getDefaultMessage()));
        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.UNAUTHORIZED, "Bad Credentials exception", ex.getMessage(), request);
        return problemDetail;
    }

    @ExceptionHandler(NotificationException.class)
    public ProblemDetail handleNotificationException(NotificationException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Notification exception", ex.getMessage(), request);
        return problemDetail;
    }

    // Tạo ProblemDetail với các trường tùy chỉnh, không có `path`, chỉ có `instance`
    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", new Date());
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", ""))); // Thiết lập `instance`

        // Thêm headers vào nếu cần thiết
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().forEachRemaining(headerName -> headers.put(headerName, request.getHeader(headerName)));
        problemDetail.setProperty("headers", headers);

        return problemDetail;
    }

    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleEmailExistsException(EmailExistsException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.CONFLICT, "Email Exists", ex.getMessage(), request);
    }
}