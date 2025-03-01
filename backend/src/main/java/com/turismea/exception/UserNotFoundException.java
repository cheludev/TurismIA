package com.turismea.exception;

public class UserNotFoundException extends RuntimeException {
    private Long id;

    public UserNotFoundException(Long id) {
        super("User " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}