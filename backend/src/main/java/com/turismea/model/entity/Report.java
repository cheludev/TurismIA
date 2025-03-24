package com.turismea.model.entity;

import com.turismea.model.enumerations.ReportType;
import com.turismea.model.enumerations.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToOne
    private Route route;

    @OneToOne
    private Tourist tourist;

    @Column(columnDefinition = "TEXT")
    @Lob
    private String description;

    @Column(nullable = false)
    private RequestStatus requestStatus;

    private ReportType type;


    public Report(Admin admin, Route route, Tourist tourist, String description) {
        this.admin = admin;
        this.route = route;
        this.tourist = tourist;
        this.description = description;
        this.requestStatus = RequestStatus.PENDING;
    }

}
