package com.turismea.model.dto.routesDTO;

import java.util.List;

public class RouteRequestDTO {
    private List<WayPoint> origins;
    private List<WayPoint> destinations;
    private String travelMode;
    private String routingPreference;

    public RouteRequestDTO(List<WayPoint> origins, List<WayPoint> destinations) {
        this.origins = origins;
        this.destinations = destinations;
        this.travelMode = "DRIVE";
        this.routingPreference = "TRAFFIC_AWARE";
    }

    public List<WayPoint> getOrigins() {
        return origins;
    }

    public List<WayPoint> getDestinations() {
        return destinations;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public String getRoutingPreference() {
        return routingPreference;
    }
}
