package com.ecommerce.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper — provides a consistent JSON structure for ALL API responses.
 *
 * <p>Every endpoint in the application returns data wrapped in this envelope, making it
 * easy for frontend developers to handle responses uniformly.</p>
 *
 * <h3>Response format:</h3>
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "Operation successful",
 *   "data": { ... },          // null for error responses
 *   "timestamp": "2025-01-15T10:30:00"
 * }
 * }</pre>
 *
 * @param <T> the type of data payload (e.g., UserResponse, List&lt;ProductResponse&gt;)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Omit null fields from JSON output
public class ApiResponse<T> {

    /** Whether the operation was successful */
    private boolean success;

    /** Human-readable message describing the result */
    private String message;

    /** The actual response data — null for error responses */
    private T data;

    /** When this response was generated */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ======================== FACTORY METHODS ========================

    /**
     * Creates a success response with data and a message.
     *
     * @param data    the response payload
     * @param message descriptive message
     * @param <T>     type of the data
     * @return a success ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a success response with data and a default message.
     *
     * @param data the response payload
     * @param <T>  type of the data
     * @return a success ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    /**
     * Creates an error response with a message and no data.
     *
     * @param message error description
     * @param <T>     type parameter (will be null)
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
