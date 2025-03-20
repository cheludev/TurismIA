package com.turismea.model.dto.routesDTO;

public class LocationWayPoint {
    private LatLng location;

    public LocationWayPoint() {
    }

    public LocationWayPoint(LatLng latLng) {
        this.location = latLng;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
