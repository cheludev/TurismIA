package com.turismea.exception;

public class SpotNotFoundException extends RuntimeException {
    private Long id;

    public SpotNotFoundException(Long id) {
        super("Spot " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
