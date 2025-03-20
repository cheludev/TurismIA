package com.turismea.model.dto.routesDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Waypoint {
    // Este campo se serializa con la clave "waypoint"
    @JsonProperty("waypoint")
    private LocationWayPoint waypoint;

    @JsonIgnore
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RouteModifiers routeModifiers;

    // Constructor por defecto
    public Waypoint() {
    }

    // Constructor con solo ubicación
    public Waypoint(LocationWayPoint waypoint) {
        this.waypoint = waypoint;
    }

    // Constructor con nombre y ubicación
    public Waypoint(String name, LocationWayPoint waypoint) {
        this.name = name;
        this.waypoint = waypoint;
    }

    // Constructor con ubicación y modificadores de ruta
    public Waypoint(LocationWayPoint waypoint, RouteModifiers routeModifiers) {
        this.waypoint = waypoint;
        this.routeModifiers = routeModifiers;
    }

    // Getters y setters
    public LocationWayPoint getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(LocationWayPoint waypoint) {
        this.waypoint = waypoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RouteModifiers getRouteModifiers() {
        return routeModifiers;
    }

    public void setRouteModifiers(RouteModifiers routeModifiers) {
        this.routeModifiers = routeModifiers;
    }
}
