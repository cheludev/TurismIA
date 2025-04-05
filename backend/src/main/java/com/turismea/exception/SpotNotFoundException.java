package com.turismea.exception;

public class SpotNotFoundException extends RuntimeException {
    private Long id;

    public SpotNotFoundException(Long id) {
        super("Spot " + id + " not found");
        this.id = id;
    }

    public SpotNotFoundException() {
        super("You have tried to found a spot, but it is not found");
    }

    public SpotNotFoundException(String s) {
        super(s);
    }

    public Long getId() {
        return id;
    }
}
