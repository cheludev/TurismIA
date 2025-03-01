package com.turismea.exception;

public class RouteNotFoundException extends RuntimeException {
    private Long id;

    public RouteNotFoundException(Long id) {
        super("Route " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
