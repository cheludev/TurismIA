package com.turismea.controller;

import com.turismea.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(NotTheOwnerOfRouteException.class)
    public ResponseEntity<String> handlerNotTheOwner() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of the route.");
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<String> handlerCityNotFound(CityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The city with ID:" + ex.getId() + " does not exist.");
    }

    @ExceptionHandler(MissingProvinceException.class)
    public ResponseEntity<String> handlerMissingProvince(MissingProvinceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The province with ID:" + ex.getProvince() + " does not exist.");
    }

    //------------------------

    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<String> handlerSpotNotFound(SpotNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Spot " + ex.getId() + "does not found");
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<String> handlerRequestNotFound(RequestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The request with ID:" + ex.getId() + " does not exist.");
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<String> handlerReportNotFound(ReportNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The report with ID:" + ex.getId() + " does not exist.");
    }

    @ExceptionHandler(AlreadyAppliedException.class)
    public ResponseEntity<String> handleAlreadyApplied(AlreadyAppliedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }



}


