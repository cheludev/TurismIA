package com.turismea.model.api_response;


public class ApiResponse<T> {
    private String status;
    private String message;
    private T body;

    public ApiResponse(String status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public ApiResponse(String status, String message) {
        this(status, message, null);
    }

    // Getters y setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
