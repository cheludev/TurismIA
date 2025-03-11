package com.turismea.exception;

import com.turismea.model.enumerations.RequestType;

public class AlreadyAppliedException extends RuntimeException {
    public AlreadyAppliedException(Long userId, RequestType type) {
        super("The user with ID: " + userId + " has already applied to " +
                (type.equals(RequestType.TO_MODERATOR) ? "moderator" : "change the province"));
    }
}
