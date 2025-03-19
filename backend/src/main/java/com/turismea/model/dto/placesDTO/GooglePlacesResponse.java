package com.turismea.model.dto.placesDTO;

import java.util.List;

public class GooglePlacesResponse {
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
