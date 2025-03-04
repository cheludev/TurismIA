package com.turismea.model;


import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "admin")
public class Admin extends User {


    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY) //We mark it as LAZY to avoid performance problems.
    private List<Report> reportList;
    @OneToMany
    private List<Tourist> appliedToModerator;
    @OneToMany
    private List<Moderator> appliedToChangeTheProvince;

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