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


    public Admin(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setEmail(user.getEmail());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setPhoto(user.getPhoto());
        this.setProvince(user.getProvince());
        this.setRole(user.getRole());
        this.setAppliedToModerator(this.appliedToModerator);
        this.setAppliedToChangeTheProvince(this.appliedToChangeTheProvince);
    }
}
