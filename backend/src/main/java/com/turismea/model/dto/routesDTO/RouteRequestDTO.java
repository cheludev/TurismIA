package com.turismea.model.dto.routesDTO;

import java.util.List;

public class RouteRequestDTO {
    private List<Waypoint> origins;
    private List<Waypoint> destinations;
    private String travelMode;

    public RouteRequestDTO(List<Waypoint> origins, List<Waypoint> destinations) {
        this.origins = origins;
        this.destinations = destinations;
        this.travelMode = "WALK";
    }
    public RouteRequestDTO(List<Waypoint> origins, List<Waypoint> destinations, String travelMode) {
        this.origins = origins;
        this.destinations = destinations;
        this.travelMode = travelMode;
    }

    public List<Waypoint> getOrigins() {
        return origins;
    }

    public void setOrigins(List<Waypoint> origins) {
        this.origins = origins;
    }

    public List<Waypoint> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Waypoint> destinations) {
        this.destinations = destinations;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

}
