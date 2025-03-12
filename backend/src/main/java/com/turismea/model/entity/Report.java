package com.turismea.model.entity;

import com.turismea.model.enumerations.ReportType;
import com.turismea.model.enumerations.RequestStatus;
import jakarta.persistence.*;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToOne
    private Route route;

    @OneToOne
    private Tourist tourist;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    @Column(nullable = false)
    private RequestStatus requestStatus;

    private ReportType type;


    public Report(Admin admin, Route route, Tourist tourist, String description) {
        this.admin = admin;
        this.route = route;
        this.tourist = tourist;
        this.description = description;
        this.requestStatus = RequestStatus.PENDING;
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

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin assignedTo) {
        this.admin = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return requestStatus;
    }

    public void setStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

}
