package com.turismea.model.dto.routesDTO;

import com.turismea.model.dto.placesDTO.Location;

public class LocationWayPoint {

    private LatLng latLng;

    public LocationWayPoint(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
