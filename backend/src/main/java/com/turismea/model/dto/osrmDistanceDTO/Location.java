package com.turismea.model.dto.osrmDistanceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor

public class Location {

    private Double latitude;
    private Double longitude;

}
