package com.turismea.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tourist")
public class Moderator extends User{

    @Column(nullable = false)
    private String province;

    @OneToMany
    @JoinColumn(name = "city_id")
    private City city;

    public Moderator(String province, City city) {
        this.province = province;
        this.city = city;
    }

    // Default constructor
    public Moderator() {}

    // Getters and Setters

    public void setCity(City city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}