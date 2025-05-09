package com.turismea.model.dto.PlacesDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Point {
    private double lat;
    private double lng;

    public Point() {
    }

    public Point(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

}
