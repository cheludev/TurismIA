package com.turismea.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city", orphanRemoval = true)
    @Column(name = "spots")
    @JsonIgnore
    private List<Spot> spots;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "city" , orphanRemoval = true)
    @Column(name = "moderators")
    @JsonIgnore
    private List<Moderator> moderators;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "city", orphanRemoval = true)
    @Column(name = "routes")
    @JsonIgnore
    private List<Route> routes;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "city", orphanRemoval = true)
    @Column(name = "distance_matrix")
    @JsonIgnore
    private List<CityDistance> distanceMatrix;

    public City(String name, List<Spot> spots, List<Moderator> moderators, List<Route> routes) {
        this.name = name;
        this.spots = spots;
        this.moderators = moderators;
        this.routes = routes;
    }

    public City(String name) {
        this.name = name;
        this.spots = new ArrayList<>();
        this.moderators = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

}
