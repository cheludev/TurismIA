package com.turismea.model.dto.routesDTO;

import java.util.List;

public class RouteRequestDTO {
    private List<Waypoint> origins;
    private List<Waypoint> destinations;
    private String travelMode;
    private String routingPreference;

    // Constructor que asigna valores por defecto para travelMode y routingPreference
    public RouteRequestDTO(List<Waypoint> origins, List<Waypoint> destinations) {
        this.origins = origins;
        this.destinations = destinations;
        this.travelMode = "DRIVE";
        this.routingPreference = "TRAFFIC_AWARE";
    }

    // Constructor completo (si fuera necesario cambiar los valores por defecto)
    public RouteRequestDTO(List<Waypoint> origins, List<Waypoint> destinations, String travelMode, String routingPreference) {
        this.origins = origins;
        this.destinations = destinations;
        this.travelMode = travelMode;
        this.routingPreference = routingPreference;
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

    public String getRoutingPreference() {
        return routingPreference;
    }

    public void setRoutingPreference(String routingPreference) {
        this.routingPreference = routingPreference;
    }
}
