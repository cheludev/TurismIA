package com.turismea.exception;

public class LocationNotFoundException extends RuntimeException {
    private Long id;

    public LocationNotFoundException(Long id) {
        super("Location " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
