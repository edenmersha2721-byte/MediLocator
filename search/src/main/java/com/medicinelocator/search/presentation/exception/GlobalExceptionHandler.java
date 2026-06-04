package com.medicinelocator.search.presentation.exception;

import com.medicinelocator.search.domain.exception.InvalidSearchRequestException;
import com.medicinelocator.search.domain.exception.NoPharmaciesFoundException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "One or more fields have validation errors",
                request.getRequestURI(), LocalDateTime.now(), fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            fieldErrors.put(field, cv.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "Request parameter validation failed",
                request.getRequestURI(), LocalDateTime.now(), fieldErrors));
    }

    @ExceptionHandler(InvalidSearchRequestException.class)
    public ResponseEntity<ErrorDetails> handleInvalidSearch(
            InvalidSearchRequestException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "Invalid Search Request", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(NoPharmaciesFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoPharmacies(
            NoPharmaciesFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(
                HttpStatus.NOT_FOUND.value(), "No Results Found", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled search service exception at {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @Schema(description = "Structured error response")
    public record ErrorDetails(
            @Schema(example = "400")       int status,
            @Schema(example = "Bad Request") String error,
            @Schema(example = "Search query is required") String message,
            @Schema(example = "/api/v1/search") String path,
            LocalDateTime timestamp,
            Map<String, String> fieldErrors
    ) {}
}