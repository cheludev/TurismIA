package com.turismea.model.dto.RouteDTO;

import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data

public class RouteListDTO {
    Long id;
    String name;
    List<Long> spots;
    Long owner;
    int duration;
    double rating;
    public RouteListDTO(Long id, String name, List<Long> spots, Long owner, int duration, double rating) {
        this.id = id;
        this.name = name;
        this.spots = spots;
        this.owner = owner;
        this.duration = duration;
        this.rating = rating;
    }

    public RouteListDTO(Route route) {
        this.id = route.getId();
        this.name = route.getName();
        this.spots = route.getSpots() != null
                ? route.getSpots().stream().map(Spot::getId).toList()
                : List.of();
        this.owner = route.getOwner() != null ? route.getOwner().getId() : null;
        this.duration = (int) route.getDuration(); // si duration es long en Route y aquí es int
        this.rating = route.getRate();
    }



}
