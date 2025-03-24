package com.turismea.model.entity;

import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Request> request = new ArrayList<>();

    @Lob
    @Column
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Province province;

    public User(String username, String email, String password, byte[] photo, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.role = role;
    }

}
