package com.turismea.model.dto.routesDTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.turismea.model.dto.placesDTO.Location;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WayPoint {
    private LocationWayPoint location;
    private String name;

    public WayPoint(String name, LocationWayPoint location) {
        this.name = name;
        this.location = location;
    }

    public LocationWayPoint getLocation() {
        return location;
    }

    public void setLocation(LocationWayPoint location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
