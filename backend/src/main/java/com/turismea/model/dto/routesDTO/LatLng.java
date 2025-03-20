package com.turismea.model.dto.routesDTO;

public class LatLng {
    private Coordinates latLng;

    public LatLng() {
    }

    public LatLng(Coordinates latLng) {
        this.latLng = latLng;
    }

    public Coordinates getLatLng() {
        return latLng;
    }

    public void setLatLng(Coordinates latLng) {
        this.latLng = latLng;
    }
}
