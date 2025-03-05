package com.turismea.model;


import com.turismea.model.enumerations.Province;
import jakarta.persistence.*;

@Entity
@Table(name = "moderator")
public class Moderator extends User{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Province province;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne
    @JoinColumn(nullable = false)
    private Request changeProvinceRequest;

    public Moderator(Province province, City city) {
        this.province = province;
    }

    // Default constructor
    public Moderator() {}

    // Getters and Setters

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}