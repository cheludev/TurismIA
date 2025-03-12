package com.turismea.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "city_distances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "location_a_id", "location_b_id"}),
        indexes = {
                @Index(name = "idx_location_pair", columnList = "location_a_id, location_b_id")
        })
public class CityDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_a_id", nullable = false)
    private Spot spotA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_b_id", nullable = false)
    private Spot spotB;

    @Column(nullable = false)
    private int distance;

    @Column(nullable = false)
    private int duration;

    public CityDistance() {}

    public CityDistance(City city, Spot spotA, Spot spotB, int distance, int duration) {
        // Avoid duplicates in reverse order
        if (spotA.getId() > spotB.getId()) {
            Spot temp = spotA;
            spotA = spotB;
            spotB = temp;
        }

        this.city = city;
        this.spotA = spotA;
        this.spotB = spotB;
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

    public Spot getSpotA() {
        return spotA;
    }

    public void setSpotA(Spot spotA) {
        this.spotA = spotA;
    }

    public Spot getSpotB() {
        return spotB;
    }

    public void setSpotB(Spot spotB) {
        this.spotB = spotB;
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
