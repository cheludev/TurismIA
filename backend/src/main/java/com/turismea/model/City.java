package com.turismea.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city", orphanRemoval = true)
    private List<Location> locations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city" , orphanRemoval = true)
    private List<Moderator> moderators;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "city", orphanRemoval = true)
    private List<Route> routes;

    public City(Long id, String name, List<Location> locations, List<Moderator> moderators, List<Route> routes) {
        this.id = id;
        this.name = name;
        this.locations = locations;
        this.moderators = moderators;
        this.routes = routes;
    }

    public City() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Moderator> getModerators() {
        return moderators;
    }

    public void setModerators(List<Moderator> moderators) {
        this.moderators = moderators;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
