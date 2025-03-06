package com.turismea.model;

import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.RequestStatus;
import jakarta.persistence.*;

@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_admin")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    private String reasonsOfTheRequest;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    private Province province;

    private RequestStatus requestStatus;

    public Request(User user, RequestType type, String reasonsOfTheRequest, Province province) {
        this.user = user;
        this.reasonsOfTheRequest = reasonsOfTheRequest;
        this.requestStatus = RequestStatus.PENDING;
        this.province = province;
    }

    public Request() {

    }

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

    public RequestStatus getStatus() {
        return this.requestStatus;
    }

    public void setStatus(RequestStatus requestStatus) {
    }
}

