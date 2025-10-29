package com.proyecto_backend.FoodHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Usaremos 500 Internal Server Error para problemas de subida
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileUploadException extends RuntimeException {
  public FileUploadException(String message) {
    super(message);
  }

  public FileUploadException(String message, Throwable cause) {
    super(message, cause);
  }
}