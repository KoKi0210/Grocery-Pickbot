package com.example.grocerypickbot.exceptions;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 *
 * <p>Catches and processes exceptions thrown by controller methods, providing
 * consistent error responses for validation errors, illegal arguments, and
 * unexpected exceptions across the application.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles MethodArgumentNovValidException and returns a 400 Bad Request.
   *
   * @param ex the MethodArgumentNotValidException instance
   * @return ResponseEntity with status 400 and a map of field errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    ex.printStackTrace();
    Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * Handles IllegalArgumentException and returns a 400 Bad Request.
   *
   * @param ex the IllegalArgumentException instance
   * @return ResponseEntity with status 400 and the exception message
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    ex.printStackTrace();
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  /**
   * Handles SecurityException and returns a 403 Forbidden.
   *
   * @param ex the SecurityException instance
   * @return ResponseEntity with status 403 and the exception message
   */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<String> handleSecurityException(SecurityException ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  /**
   * Handles UnauthorizedException and returns a 401 Unauthorized.
   *
   * @param ex the UnauthorizedException instance
   * @return ResponseEntity with status 401 and the exception message
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException ex) {
    ex.printStackTrace();
    Map<String, String> errors = ex.getErrors();
    if (errors == null || errors.isEmpty()) {
      errors = Map.of("general", ex.getMessage());
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
  }

  /**
   * Handles all other exceptions and returns a 500 Internal Server Error.
   *
   * @param ex the Exception instance
   * @return ResponseEntity with status 500 and a generic error message
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Unexpected error occurred: " + ex.getMessage());
  }
}