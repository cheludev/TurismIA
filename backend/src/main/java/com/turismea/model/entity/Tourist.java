package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tourist")
@Getter
@Setter
@NoArgsConstructor
public class Tourist extends User {

    // Rutas que el turista HA CREADO (es el owner)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Route> createdRoutes = new ArrayList<>();

    // Rutas que el turista HA GUARDADO (favoritas)
    @ManyToMany
    @JoinTable(
            name = "tourist_saved_routes",
            joinColumns = @JoinColumn(name = "tourist_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id")
    )
    private List<Route> savedRoutes = new ArrayList<>();

    @OneToOne
    private Request promoteToModeratorRequest;

    public Tourist(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setEmail(user.getEmail());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setPhoto(user.getPhoto());
        this.setProvince(user.getProvince());
        this.setRole(user.getRole());
        this.setSavedRoutes(new ArrayList<>());
        this.setCreatedRoutes(new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tourist tourist = (Tourist) o;
        return Objects.equals(this.getUsername(), tourist.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
