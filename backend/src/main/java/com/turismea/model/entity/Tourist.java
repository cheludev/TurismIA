package com.turismea.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tourist tourist = (Tourist) o;
        return Objects.equals(this.getUsername(), tourist.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
