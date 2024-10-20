package org.example.sem_backend.modules.room_module.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice(basePackages = "org.example.sem_backend.modules.room_module")
public class RoomExceptionHandler {

    @ExceptionHandler(RoomUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleRoomUnavailableException(RoomUnavailableException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, HttpStatusCode.valueOf(409), "Phòng không khả dụng cho thời gian yêu cầu.");
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(409));
    }
}
