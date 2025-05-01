package com.turismea.model.entity;


import com.turismea.model.enumerations.Province;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "moderator")
@Getter
@Setter
@NoArgsConstructor
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


    public Moderator(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setEmail(user.getEmail());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setPhoto(user.getPhoto());
        this.setProvince(user.getProvince());
        this.setRole(user.getRole());
        this.setChangeProvinceRequest(this.changeProvinceRequest);
    }
}