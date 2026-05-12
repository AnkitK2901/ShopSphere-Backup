package com.shopsphere.inventory.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException; // THE FIX: Added Import
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
                        ResourceNotFoundException exception, WebRequest webRequest) {
                log.warn("Resource Not Found: {}", exception.getMessage());
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                exception.getMessage(),
                                webRequest.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<ErrorDetails> handleInsufficientStockException(
                        InsufficientStockException exception, WebRequest webRequest) {
                log.warn("Business Rule Violation: {}", exception.getMessage());
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                exception.getMessage(),
                                webRequest.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        // ==============================================================
        // THE FIX: Catch Optimistic Locking Exceptions for Race Conditions
        // ==============================================================
        @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
        public ResponseEntity<ErrorDetails> handleOptimisticLockingFailure(
                        ObjectOptimisticLockingFailureException exception, WebRequest webRequest) {
                log.warn("Concurrent Checkout Conflict Detected: {}", exception.getMessage());
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                "Sorry, this item was just purchased by another user. Please try again.",
                                webRequest.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // Returns 409 Conflict
        }

        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationExceptions(
                        org.springframework.web.bind.MethodArgumentNotValidException exception) {
                log.warn("Input Validation Failed");
                java.util.Map<String, String> errors = new java.util.HashMap<>();
                exception.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        // Always keep the generic Exception handler at the very bottom
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDetails> handleGlobalException(
                        Exception exception, WebRequest webRequest) {
                log.error("CRITICAL SERVER ERROR: ", exception);
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                "An unexpected internal server error occurred",
                                webRequest.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}