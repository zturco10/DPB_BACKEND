package com.proyecto_backend.FoodHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN) // Esto devuelve un 403 Forbidden
public class AccesoNoAutorizadoException extends RuntimeException {
    public AccesoNoAutorizadoException(String message) {
        super(message);
    }
}