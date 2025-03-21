package com.turismea.model.dto.routesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleRouteResponse {

    @JsonProperty("originIndex")
    private Integer originIndex;

    @JsonProperty("destinationIndex")
    private Integer destinationIndex;

    @JsonProperty("distanceMeters")
    private Integer distanceMeters;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("status")
    private Status status;

    public GoogleRouteResponse() {
    }

    public GoogleRouteResponse(Integer originIndex, Integer destinationIndex, Integer distanceMeters, Status status, String duration, String condition) {
        this.originIndex = originIndex;
        this.destinationIndex = destinationIndex;
        this.distanceMeters = distanceMeters;
        this.status = status;
        this.duration = duration;
        this.condition = condition;
    }

    public Integer getOriginIndex() {
        return originIndex;
    }

    public void setOriginIndex(Integer originIndex) {
        this.originIndex = originIndex;
    }

    public Integer getDestinationIndex() {
        return destinationIndex;
    }

    public void setDestinationIndex(Integer destinationIndex) {
        this.destinationIndex = destinationIndex;
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Integer distanceMeters) {
        this.distanceMeters = distanceMeters;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
