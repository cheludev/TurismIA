package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoutePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Route route;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String polylineJson;
}
