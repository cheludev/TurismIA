package com.turismea.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Tourist owner;

    private int rate;

    @OneToOne(mappedBy = "route")
    private Report report;

    @ManyToMany
    @JoinTable(
            name = "route_location",  // Intermediate name table
            joinColumns = @JoinColumn(name = "route_id"),  // Foreign key -> Route
            inverseJoinColumns = @JoinColumn(name = "location_id") // Foreign key -> Spot
    )
    private List<Spot> spots; //We create an intermediate table which contains the relation between Routes and they spots.

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    public Route(Long id, String name, City city, Tourist owner, int rate, List<Spot> spots, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.owner = owner;
        this.rate = rate;
        this.spots = spots;
        this.description = description;
    }

    public Route() {

    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
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

    public List<Spot> getPath() {
        return spots;
    }

    public void setPath(List<Spot> path) {
        this.spots = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }

}
