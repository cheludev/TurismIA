package com.turismea.model.dto.RouteDTO;

import com.turismea.model.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {
    private LocationDTO from;
    private LocationDTO to;
    private int maxDuration;
}
