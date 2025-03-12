package com.turismea.service;

import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final GoogleMapsService googleMapsService;

    public SpotService(SpotRepository spotRepository, GoogleMapsService googleMapsService) {
        this.spotRepository = spotRepository;
        this.googleMapsService = googleMapsService;
    }

    public void validateSpot(Spot spot){
        spotRepository.findById(spot.getId())
                        .orElseThrow(() -> new SpotNotFoundException(spot.getId()));
        spot.setValidated(true);
        spotRepository.save(spot);

    }

    public Spot newTouristicSpot(Long locationId, String touristicInfo) {
        Spot spot = spotRepository.findById(locationId)
                .orElseThrow(() -> new SpotNotFoundException(locationId));

        if(!spot.isValidated()) {
            spot.setValidated(true);
        }

        spot.setInfo(touristicInfo);
        return spot;
    }

    public void saveCitySpots(String city) {
        googleMapsService.getSpots(city).subscribe(json -> {

        });
    }

    public void deleteSpot(Spot spot){
        spotRepository.delete(spot);
    }

    public List<Spot> getValidatedSpotByCity(City city) {
        return spotRepository.getSpotByValidatedAndCity(true, city);
    }

    public List<Spot> getAllSpotByCity(City city) {
        return spotRepository.getSpotByCity(city);
    }

}
