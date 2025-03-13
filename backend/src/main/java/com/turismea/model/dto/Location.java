package com.turismea.model.dto;

public class Location {
    private final Double latitude;
    private final Double longitude;

    public Location(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
