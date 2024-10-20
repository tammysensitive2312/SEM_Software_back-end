package org.example.sem_backend.main_service.middleware.exception;

import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý các ngoại lệ chung không dự đoán trước
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(500), "Đã xảy ra lỗi trên server. Vui lòng thử lại sau.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(500));
    }

    // Xử lý lỗi không tìm thấy tài nguyên toàn cục
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(404), "Tài nguyên không được tìm thấy.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }

    // Xử lý lỗi xung đột tài nguyên (toàn cục)
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(409), "Xung đột dữ liệu. Vui lòng kiểm tra lại yêu cầu.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(409));
    }

    // Xử lý lỗi yêu cầu không hợp lệ - Method Argument Not Valid (toàn cục)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("Yêu cầu không hợp lệ. Vui lòng kiểm tra lại thông tin.");

        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(400), errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(400));
    }

    // Xử lý lỗi không đọc được HTTP Message (Invalid JSON hoặc yêu cầu không đúng định dạng)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(400), "Yêu cầu không hợp lệ hoặc định dạng dữ liệu không đúng.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(400));
    }
}
