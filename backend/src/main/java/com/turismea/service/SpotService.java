package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.dto.GooglePlacesResponse;
import com.turismea.model.dto.Place;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityRepository;
import com.turismea.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final GoogleSpotService googleSpotService;
    private final CityRepository cityRepository;

    public SpotService(SpotRepository spotRepository, GoogleSpotService googleSpotService, CityRepository cityRepository) {
        this.spotRepository = spotRepository;
        this.googleSpotService = googleSpotService;
        this.cityRepository = cityRepository;
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
        googleSpotService.getSpots(city).subscribe(jsonResponse -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                GooglePlacesResponse response = objectMapper.readValue(jsonResponse, GooglePlacesResponse.class);

                List<Place> places = response.getPlaces();
                for (Place place : places) {
                    Spot spot = new Spot();
                    spot.setName(place.getName());
                    spot.setAddress(place.getAddress());
                    spot.setLatitude(place.getLatitude());
                    spot.setLongitude(place.getLongitude());

                    spot.setCity(cityRepository.findByName(city));

                    spotRepository.save(spot);
                }
                System.out.println("Places were saved satisfactory in DB");

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error processing JSON");
            }
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
