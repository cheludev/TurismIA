package com.turismea.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "admin")
public class Admin extends User {

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<Report> reportList;

    @OneToMany(mappedBy = "admin")
    private List<Request> appliedToModerator;

    @OneToMany(mappedBy = "admin")
    private List<Request> appliedToChangeTheProvince;

    // Default constructor
    public Admin() {}

    // Getters and Setters

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
}
