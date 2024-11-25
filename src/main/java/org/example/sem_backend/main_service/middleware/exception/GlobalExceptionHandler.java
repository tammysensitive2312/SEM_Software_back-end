package org.example.sem_backend.main_service.middleware.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Data Integrity Violation", ex.getRootCause().getMessage(), request);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ProblemDetail handleJpaSystemException(JpaSystemException ex, WebRequest request) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "JPA System Error", ex.getRootCause().getMessage(), request);
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
        //problemDetail.setProperty("headers", headers);

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        // Tạo danh sách chứa các validation error messages
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );

        // Thêm thông tin chi tiết về lỗi validation
        problemDetail.setProperty("errors", errorMessages);
        problemDetail.setTitle("Validation Error");

        return problemDetail;
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ProblemDetail handleOptimisticLockException(OptimisticLockException ex, WebRequest request) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Optimistic Locking Failure",
                "Thiết bị đã được mượn bởi người dùng khác",
                request
        );
    }
}
