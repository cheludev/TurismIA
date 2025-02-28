package com.turismea.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Tourist owner;

    private int rate;

    @ManyToMany
    @JoinTable(
            name = "route_location",  // Intermediate name table
            joinColumns = @JoinColumn(name = "route_id"),  // Foreign key -> Route
            inverseJoinColumns = @JoinColumn(name = "location_id") // Foreign key -> Location
    )
    private List<Location> locations; //We create an intermediate table which contains the relation between Routes and they locations.

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    public Route(Long id, String name, String city, Tourist owner, int rate, List<Location> locations, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.owner = owner;
        this.rate = rate;
        this.locations = locations;
        this.description = description;
    }

    public Route() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tourist getOwner() {
        return owner;
    }

    public void setOwner(Tourist owner) {
        this.owner = owner;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<Location> getPath() {
        return locations;
    }

    public void setPath(List<Location> path) {
        this.locations = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
