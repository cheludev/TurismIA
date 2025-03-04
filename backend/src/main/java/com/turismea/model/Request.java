package com.turismea.model;

import jakarta.persistence.*;

@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    // Getters y Setters
    public User getUser() {
        return user;
    }

    public RequestType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

