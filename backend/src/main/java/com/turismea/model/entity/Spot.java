package com.turismea.model.entity;

import com.turismea.model.dto.LocationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Double rating;
    private int averageTime;
    private boolean validated;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinates;


    @Column(columnDefinition = "TEXT")
    @Lob
    private String info;


    @ManyToMany(mappedBy = "spots")
    private List<Route> routes;

    public Spot(String name, City city, String address, Double latitude,
                Double longitude, int averageTime, boolean validated,
                String info, List<Route> routes, Double rating, Point coordinates) {

        this.rating = rating;
        this.name = name;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageTime = averageTime;
        this.validated = validated;
        this.info = info;
        this.routes = routes;
        this.coordinates = coordinates;
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

    public Spot(String finalOrInitial, Double latitude, Double longitude) {
        this.name = finalOrInitial;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageTime = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spot spot = (Spot) o;
        return Objects.equals(id, spot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString(){
        return this.getName();
    }


}
