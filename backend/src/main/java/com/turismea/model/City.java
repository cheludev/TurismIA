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
    @Column(name = "spots")
    private List<Spot> spots;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city" , orphanRemoval = true)
    @Column(name = "moderators")
    private List<Moderator> moderators;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "city", orphanRemoval = true)
    @Column(name = "routes")
    private List<Route> routes;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "city", orphanRemoval = true)
    @Column(name = "distance_matrix")
    private List<CityDistance> distanceMatrix;

    public City(String name, List<Spot> spots, List<Moderator> moderators, List<Route> routes) {
        this.name = name;
        this.spots = spots;
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

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
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
