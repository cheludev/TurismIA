package com.turismea.model.api_response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtils {

    // Success - body
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T body) {
        return ResponseEntity.ok(new ApiResponse<>("success", message, body));
    }

    // Success - without body
    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(new ApiResponse<>("success", message));
    }


    // Created (201)
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("created", message, body));
    }


    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value() + "", message, null));
    }



    // Conflict genérico
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message, T body) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("error", message, body));
    }


    // Error 401 (Unauthorized)
    public static ResponseEntity<ApiResponse<Void>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("error", message));
    }

    // Error 403 (Forbidden)
    public static ResponseEntity<ApiResponse<Void>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>("error", message));
    }

    // Error 404 (Not Found)
    public static ResponseEntity<ApiResponse<Void>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("error", message));
    }

    // Error 409 (Conflict)
    public static ResponseEntity<ApiResponse<Void>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("error", message));
    }

    // Error 500 (Internal Server Error)
    public static ResponseEntity<ApiResponse<Void>> internalServerError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("error", message));
    }
}
