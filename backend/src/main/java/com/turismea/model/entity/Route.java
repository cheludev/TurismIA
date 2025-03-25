package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    private LinkedList<Spot> spots; //We create an intermediate table which contains the relation between Routes and they spots.

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    public Route(Long id, String name, City city, Tourist owner, int rate, LinkedList<Spot> spots, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.owner = owner;
        this.rate = rate;
        this.spots = spots;
        this.description = description;
    }

}
