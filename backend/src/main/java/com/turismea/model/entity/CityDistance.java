package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "city_distances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "location_a_id", "location_b_id"}),
        indexes = {
                @Index(name = "idx_location_pair", columnList = "location_a_id, location_b_id")
        })
@Getter
@Setter
@NoArgsConstructor
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
    private long distance;

    @Column(nullable = false)
    private long duration;

    public CityDistance(City city, Spot spotA, Spot spotB, long distance, long duration) {
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

}
