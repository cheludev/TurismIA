package com.turismea.exception;

public class ReportNotFoundException extends RuntimeException {
    private final Long id;

    public ReportNotFoundException(Long id) {
        super("Report " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
