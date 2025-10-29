package com.proyecto_backend.FoodHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Usaremos 500 por ahora.
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class NotificationSendException extends RuntimeException {
    public NotificationSendException(String message) {
        super(message);
    }

    public NotificationSendException(String message, Throwable cause) {
        super(message, cause);
    }
}