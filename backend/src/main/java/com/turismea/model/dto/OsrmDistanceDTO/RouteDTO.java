package com.turismea.model.dto.OsrmDistanceDTO;

import com.turismea.model.dto.RouteDTO.Geometry;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RouteDTO {
    private long distance;
    private long duration;
    private Geometry geometry;
}

