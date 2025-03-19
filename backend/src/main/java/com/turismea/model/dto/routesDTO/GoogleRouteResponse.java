package com.turismea.model.dto.routesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleRouteResponse {

    private int originIndex;
    private int destinationIndex;
    private int distanceMeters;
    private Status status;
    private String duration;
    private String condition;

    public GoogleRouteResponse(int originIndex, int destinationIndex, int distanceMeters, Status status, String duration, String condition) {
        this.originIndex = originIndex;
        this.destinationIndex = destinationIndex;
        this.distanceMeters = distanceMeters;
        this.status = status;
        this.duration = duration;
        this.condition = condition;
    }

    public int getOriginIndex() {
        return originIndex;
    }

    public void setOriginIndex(int originIndex) {
        this.originIndex = originIndex;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }

    public void setDestinationIndex(int destinationIndex) {
        this.destinationIndex = destinationIndex;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
