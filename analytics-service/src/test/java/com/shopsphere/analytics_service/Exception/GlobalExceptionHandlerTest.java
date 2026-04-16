package com.shopsphere.analytics_service.Exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResourceNotFoundException → 404 with message")
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Order not found");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Order not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("IllegalArgumentException → 400 with message")
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid customer ID");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid customer ID");
    }

    @Test
    @DisplayName("Generic Exception → 500 with wrapped message")
    void handleGenericException() {
        Exception ex = new RuntimeException("Database connection lost");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage())
                .isEqualTo("An unexpected error occurred: Database connection lost");
    }

    @Test
    @DisplayName("NullPointerException → 500 (caught by generic handler)")
    void handleNullPointerException() {
        NullPointerException ex = new NullPointerException("null reference");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("null reference");
    }
}
