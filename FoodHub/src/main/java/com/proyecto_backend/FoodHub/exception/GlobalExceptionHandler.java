package com.proyecto_backend.FoodHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioYaExiste(UserAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "El usuario ya existe", ex.getMessage());
    }

    @ExceptionHandler(Exception.class) // captura cualquier otra excepción no controlada
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", ex.getMessage());
    }

    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public ResponseEntity<Map<String, Object>> handleAccesoNoAutorizado(AccesoNoAutorizadoException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acceso Denegado", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        // Devolvemos explícitamente 403 Forbidden
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acceso Denegado por Permisos", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        // Usamos el status definido en la excepción (401)
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales Inválidas", ex.getMessage());
    }

    @ExceptionHandler(NotificationSendException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationSendError(NotificationSendException ex) {
        // Usamos el status definido en la excepción (500)
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al Enviar Notificación", ex.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUploadException(FileUploadException ex) {

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al Subir Archivo", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // ✅ CORRECCIÓN 1: Llamar al helper que SÓLO devuelve un Map
        Map<String, Object> errorBody = buildErrorResponseMap(HttpStatus.BAD_REQUEST, "Error de Validación", errors.toString());
        errorBody.put("detalles_campos", errors); // Añade los detalles por campo
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    // ✅ CORRECCIÓN 2: Este método ahora usa el helper 'buildErrorResponseMap'
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, String error) {
        Map<String, Object> map = buildErrorResponseMap(status, message, error); // Llama al helper
        return ResponseEntity.status(status).body(map);
    }

    // ✅ CORRECCIÓN 3: Este es el nuevo helper que devuelve SÓLO el Map
    private Map<String, Object> buildErrorResponseMap(HttpStatus status, String message, String error) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now());
        map.put("status", status.value());
        map.put("error", error);
        map.put("message", message);
        return map;
    }
}