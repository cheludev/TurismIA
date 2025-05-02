package com.turismea.model.dto.PlacesDTO;

import lombok.Getter;

import java.util.List;

@Getter
public class GooglePlacesResponse {
    private List<Place> places;

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
