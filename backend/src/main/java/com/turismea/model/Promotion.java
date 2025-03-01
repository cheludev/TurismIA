package com.turismea.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Promotion extends Report {
    @Id
    private Long id;

    @OneToOne
    private Tourist tourist;

    public Promotion(Tourist user) {
        tourist = user;
    }

    public Promotion() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
