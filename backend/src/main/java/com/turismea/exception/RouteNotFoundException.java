package com.turismea.exception;

import lombok.Getter;

@Getter
public class RouteNotFoundException extends RuntimeException {
    private Long id;

    public RouteNotFoundException(Long id) {
        super("Route " + id + " not found");
        this.id = id;
    }

    public RouteNotFoundException(String s) {
        super(s);
    }

}
