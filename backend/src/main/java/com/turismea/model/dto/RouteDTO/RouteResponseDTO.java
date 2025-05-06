package com.turismea.model.dto.RouteDTO;

import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RouteResponseDTO {

    private Long id;
    private String name;
    private String description;
    private long duration;
    private double rate;
    private Long cityId;
    private Long ownerId;
    private List<Long> spotIds;
    private boolean draft;

    public RouteResponseDTO(Route route) {
        this.id = route.getId();
        this.name = route.getName();
        this.description = route.getDescription();
        this.duration = route.getDuration();
        this.rate = route.getRate();
        this.cityId = route.getCity() != null ? route.getCity().getId() : null;
        this.ownerId = route.getOwner() != null ? route.getOwner().getId() : null;
        this.spotIds = route.getSpots() != null
                ? new ArrayList<>(route.getSpots()).stream()
                .map(Spot::getId)
                .toList()
                : List.of();
        this.draft = true;

    }


}
