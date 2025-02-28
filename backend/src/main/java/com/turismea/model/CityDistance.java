package com.turismea.model;

import jakarta.persistence.*;

@Entity
@Table(name = "city_distances")
public class CityDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_a_id", nullable = false)
    private Location locationA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_b_id", nullable = false)
    private Location locationB;

    @Column(nullable = false)
    private int distance; // en metros

    @Column(nullable = false)
    private int duration; // en segundos

    public CityDistance() {}

    public CityDistance(City city, Location locationA, Location locationB, int distance, int duration) {
        this.city = city;
        this.locationA = locationA;
        this.locationB = locationB;
        this.distance = distance;
        this.duration = duration;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Location getLocationA() {
        return locationA;
    }

    public void setLocationA(Location locationA) {
        this.locationA = locationA;
    }

    public Location getLocationB() {
        return locationB;
    }

    public void setLocationB(Location locationB) {
        this.locationB = locationB;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

