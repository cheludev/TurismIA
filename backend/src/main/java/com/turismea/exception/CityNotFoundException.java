package com.turismea.exception;

public class CityNotFoundException extends RuntimeException {
    private Long id;

    public CityNotFoundException(Long id) {
        super("City " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
