package com.turismea.model;

import java.sql.Blob;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tourist")
public class Tourist extends User{
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Route> savedRoutes;

    @OneToOne
    private Promotion promotion;

    public Tourist() {
        super();
    }

    public List<Route> getSavedRoutes() { return savedRoutes; }
    public void setSavedRoutes(List<Route> routes) { this.savedRoutes = routes; }


}
