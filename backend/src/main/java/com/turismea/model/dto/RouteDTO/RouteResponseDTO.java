package com.turismea.model.dto.RouteDTO;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RouteResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Long cityId;
    private int duration;
    private double rating;
    private boolean draft;
    private Long ownerId;
    private List<Long> spotIds;

    private List<LocationDTO> polyline;

    public RouteResponseDTO(Route route) {
        this.id = route.getId();
        this.name = route.getName();
        this.description = route.getDescription();
        this.cityId = route.getCity() != null ? route.getCity().getId() : null;
        this.duration = (int) route.getDuration();
        this.rating = route.getRate();
        this.draft = route.isDraft();
        this.ownerId = route.getOwner().getId();
        this.spotIds = route.getSpots().stream()
                .map(Spot::getId)
                .collect(Collectors.toList());
    }
}
