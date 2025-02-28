package com.turismea.model;

import jakarta.persistence.*;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin assignedTo;

    @OneToOne
    private Route route;

    @OneToOne
    private Tourist tourist;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    public Report(Long id, Admin assignedTo, Route route, Tourist tourist, String description) {
        this.id = id;
        this.assignedTo = assignedTo;
        this.route = route;
        this.tourist = tourist;
        this.description = description;
    }
    public Report() {
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public Tourist getTourist() {
        return tourist;
    }

    public void setTourist(Tourist tourist) {
        this.tourist = tourist;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Admin getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Admin assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
