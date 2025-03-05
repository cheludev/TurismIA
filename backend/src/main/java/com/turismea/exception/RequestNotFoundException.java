package com.turismea.exception;

public class RequestNotFoundException extends RuntimeException {
    private Long id;

    public RequestNotFoundException(Long id) {
        super("Request " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
