package com.turismea.model.api_response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
