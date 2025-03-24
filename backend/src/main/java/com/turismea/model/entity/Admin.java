package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<Report> reportList;

    @OneToMany(mappedBy = "admin")
    private List<Request> appliedToModerator;

    @OneToMany(mappedBy = "admin")
    private List<Request> appliedToChangeTheProvince;

    public Admin(String user, String password) {
        this.setUsername(user);
        this.setPassword(password);
    }


}
