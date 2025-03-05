package com.turismea.exception;

public class NotTheOwnerOfRouteException extends RuntimeException {
    private Long ownerId, originalRouteId;

    public NotTheOwnerOfRouteException(Long ownerId, Long originalRouteId) {
        super("User " + ownerId + " is not the owner of the route with ID " + originalRouteId);
        this.originalRouteId = ownerId;
        this.ownerId = ownerId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOriginalRouteId() {
        return originalRouteId;
    }

    public void setOriginalRouteId(Long originalRouteId) {
        this.originalRouteId = originalRouteId;
    }
}
