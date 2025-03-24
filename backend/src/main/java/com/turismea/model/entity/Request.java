package com.turismea.model.entity;

import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
        this.type = type;
        this.reasonsOfTheRequest = reasonsOfTheRequest;
        this.requestStatus = RequestStatus.PENDING;
        this.province = province;
    }


}

