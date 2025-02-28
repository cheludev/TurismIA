package com.turismea.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(Long id, String username, String email, String password, byte[] photo, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.role = role;
    }

    public User() {

    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
}
