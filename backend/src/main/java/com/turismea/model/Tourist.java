package com.turismea.model;

import java.sql.Blob;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tourist")
public class Tourist extends User{
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Route> savedRoutes;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Route> createdRoutes;

    public Tourist() {
        super();
    }

    public List<Route> getSavedRoutes() { return savedRoutes; }
    public void setSavedRoutes(List<Route> routes) { this.savedRoutes = routes; }

    public List<Route> getCreatedRoutes() { return createdRoutes; }
    public void setCreatedRoutes(List<Route> createdRoutes) { this.createdRoutes = createdRoutes; }

}
