package org.example.sem_backend.modules.room_module.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class RoomUnavailableException extends RuntimeException {

    public RoomUnavailableException(String message) {
        super(message);
    }

    public RoomUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
