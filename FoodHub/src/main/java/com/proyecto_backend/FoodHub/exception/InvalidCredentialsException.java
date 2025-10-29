package com.proyecto_backend.FoodHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Devolverá un 401 Unauthorized automáticamente
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }

  public InvalidCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}