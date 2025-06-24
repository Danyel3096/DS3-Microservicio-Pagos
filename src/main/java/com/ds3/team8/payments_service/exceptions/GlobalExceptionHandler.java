package com.ds3.team8.payments_service.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Maneja excepciones de recursos no encontrados
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Mensaje de error específico
        response.put("status", HttpStatus.NOT_FOUND.value()); // Código HTTP 404

        // Retorna la respuesta con el estado 404 (Not Found)
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Maneja excepciones de solicitud incorrecta
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Mensaje de error específico
        response.put("status", HttpStatus.BAD_REQUEST.value()); // Código HTTP 400

        // Retorna la respuesta con el estado 400 (Bad Request)
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Maneja excepciones de acceso no autorizado
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Mensaje de error específico
        response.put("status", HttpStatus.UNAUTHORIZED.value()); // Código HTTP 401

        // Retorna la respuesta con el estado 401 (Unauthorized)
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Maneja excepciones de acceso prohibido
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage()); // Mensaje de error específico
        response.put("status", HttpStatus.FORBIDDEN.value()); // Código HTTP 403

        // Retorna la respuesta con el estado 403 (Forbidden)
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Maneja excepciones de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());

        // Extraer errores específicos de cada campo
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Maneja cualquier excepción genérica no controlada en la aplicación
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error inesperado: " + ex.getMessage()); // Mensaje de error con detalles
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // Código HTTP 500

        // Retorna la respuesta con el estado 500 (Internal Server Error)
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Maneja errores específicos relacionados con la base de datos
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        // Se crea un mapa para estructurar la respuesta de error
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error de acceso a datos: " + ex.getMessage()); // Mensaje de error con detalles
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // Código HTTP 500
        // Retorna la respuesta con el estado 500 (Internal Server Error)
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
