package com.turismea.model.dto.placesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Setter
public class Location {
    private Double latitude;
    private Double longitude;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Location(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public Location() {}
    }
