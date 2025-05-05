package com.turismea.controller;

import com.turismea.exception.*;
import com.turismea.model.api_response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRouteNotFound(RouteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(NotTheOwnerOfRouteException.class)
    public ResponseEntity<ApiResponse<?>> handleNotTheOwner(NotTheOwnerOfRouteException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCityNotFound(CityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(MissingProvinceException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingProvince(MissingProvinceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleSpotNotFound(SpotNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRequestNotFound(RequestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleReportNotFound(ReportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(AlreadyAppliedException.class)
    public ResponseEntity<ApiResponse<?>> handleAlreadyApplied(AlreadyAppliedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>("error", ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>("error", "Unexpected error: " + ex.getMessage())
        );
    }

}
