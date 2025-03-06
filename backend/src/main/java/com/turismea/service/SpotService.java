package com.turismea.service;

import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.City;
import com.turismea.model.Spot;
import com.turismea.model.Moderator;
import com.turismea.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;

    public SpotService(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    public void validateSpot(Spot spot){

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

    public List<Spot> getValidatedSpotByCity(City city) {
        return spotRepository.getSpotByValidatedAndCity(true, city);
    }

    public List<Spot> getAllSpotByCity(City city) {
        return spotRepository.getSpotByCity(city);
    }

}
