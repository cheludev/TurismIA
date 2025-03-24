package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Entity
@Table(name = "tourist")
@Getter
@Setter
@NoArgsConstructor
public class Tourist extends User{
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Route> savedRoutes;

    @OneToOne
    private Request promoteToModeratorRequest;

}
