package com.medicinelocator.auth.presentation.exception;

import com.medicinelocator.auth.domain.exception.AccountLockedException;
import com.medicinelocator.auth.domain.exception.EmailNotVerifiedException;
import com.medicinelocator.auth.domain.exception.InvalidTokenException;
import com.medicinelocator.auth.domain.exception.PharmacyNotApprovedException;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ErrorDetails> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorDetails details = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields have validation errors",
                request.getRequestURI(),
                LocalDateTime.now(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorDetails> handleAccountLocked(
            AccountLockedException ex, HttpServletRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Account Locked",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(details);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorDetails> handleEmailNotVerified(
            EmailNotVerifiedException ex, HttpServletRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Email Not Verified",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(details);
    }

    @ExceptionHandler(PharmacyNotApprovedException.class)
    public ResponseEntity<ErrorDetails> handlePharmacyNotApproved(
            PharmacyNotApprovedException ex, HttpServletRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Pharmacy Not Approved",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(details);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDetails> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Token",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at path={}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorDetails details = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(details);
    }

    public record ErrorDetails(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp,
            Map<String, String> fieldErrors
    ) {}
}