package com.turismea.model.dto.osrmDistanceDTO;

import lombok.Data;
import lombok.Setter;


@Setter
@Data
public class RouteDTO {
    private long distance;
    private long duration;

    public RouteDTO(long distance, long duration) {
        this.distance = distance;
        this.duration = duration;
    }

}
