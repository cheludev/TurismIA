package com.turismea.model.entity;

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
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private int googleIndex;
    private String address;
    private Double latitude;
    private Double longitude;

    private int averageTime;
    private boolean validated;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String info;


    @ManyToMany(mappedBy = "spots")
    private List<Route> routes;

    public Spot(String name, City city, String address, Double latitude,
                Double longitude, int averageTime, boolean validated,
                String info, List<Route> routes) {


        this.name = name;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageTime = averageTime;
        this.validated = validated;
        this.info = info;
        this.routes = routes;
    }

    public Spot(Spot spot) {
        this.id = spot.getId();
        this.name = spot.getName();
        this.city = spot.getCity();
        this.googleIndex = spot.getGoogleIndex();
        this.address = spot.getAddress();
        this.latitude = spot.getLatitude();
        this.longitude = spot.getLongitude();
        this.averageTime = spot.getAverageTime();
        this.validated = spot.isValidated();
        this.info = spot.getInfo();
        this.routes = new ArrayList<>(spot.getRoutes());
    }


}
