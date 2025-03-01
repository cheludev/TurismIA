package com.turismea.controller;

import com.turismea.exception.NotTheOwnerOfRouteEception;
import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Maneja excepciones globalmente para todos los controladores
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User " + ex.getId() + " not found");
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<String> handlerRouteNotFound(RouteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Route " + ex.getId() + " not found");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NotTheOwnerOfRouteEception.class)
    public ResponseEntity<String> handlerNotTheOwner() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of the route.");
    }
}


