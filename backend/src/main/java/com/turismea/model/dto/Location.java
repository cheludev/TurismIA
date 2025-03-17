package com.turismea.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Location {
    private Double latitude;
    private Double longitude;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Location(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public Location() {}

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
