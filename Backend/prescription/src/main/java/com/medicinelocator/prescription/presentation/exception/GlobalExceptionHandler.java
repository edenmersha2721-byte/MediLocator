package com.medicinelocator.prescription.presentation.exception;

import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import com.medicinelocator.prescription.domain.exception.OcrProcessingException;
import com.medicinelocator.prescription.domain.exception.PrescriptionNotFoundException;
import com.medicinelocator.prescription.domain.exception.SearchServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
                "One or more fields failed validation",
                request.getRequestURI(), LocalDateTime.now(), fieldErrors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetails> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "File Too Large",
                "Uploaded file exceeds maximum allowed size of 10 MB",
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(InvalidPrescriptionException.class)
    public ResponseEntity<ErrorDetails> handleInvalidPrescription(
            InvalidPrescriptionException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(), "Invalid Request", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(PrescriptionNotFoundException.class)
    public ResponseEntity<ErrorDetails> handlePrescriptionNotFound(
            PrescriptionNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(
                HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(OcrProcessingException.class)
    public ResponseEntity<ErrorDetails> handleOcrProcessing(
            OcrProcessingException ex, HttpServletRequest request) {
        log.error("OCR processing failed: path={} error={}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "OCR Processing Failed",
                "Failed to extract text from prescription image. Please ensure the image is clear and try again.",
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(SearchServiceUnavailableException.class)
    public ResponseEntity<ErrorDetails> handleSearchServiceUnavailable(
            SearchServiceUnavailableException ex, HttpServletRequest request) {
        log.error("Search Service unavailable: path={} error={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorDetails(
                HttpStatus.SERVICE_UNAVAILABLE.value(), "Search Service Unavailable",
                "The pharmacy search service is currently unavailable. Medicines were extracted but search results could not be retrieved.",
                request.getRequestURI(), LocalDateTime.now(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(), LocalDateTime.now(), null));
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