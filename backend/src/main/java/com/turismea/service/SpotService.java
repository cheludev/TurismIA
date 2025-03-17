package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.CityNotFoundException;
import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.dto.GooglePlacesResponse;
import com.turismea.model.dto.Place;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityRepository;
import com.turismea.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final GoogleSpotService googleSpotService;
    private final CityService cityService;

    public SpotService(SpotRepository spotRepository, GoogleSpotService googleSpotService, CityService cityService) {
        this.spotRepository = spotRepository;
        this.googleSpotService = googleSpotService;
        this.cityService = cityService;
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

                City cityEntity = new City();
                if (!cityService.findByName(city).isPresent()) {
                    cityEntity = cityService.save(new City(city));
                }
                List<Spot> spots = new ArrayList<>();
                for (Place place : places) {
                    if(spotRepository.findByName(place.getName())==null) {
                        Spot spot = new Spot(
                                place.getName(),
                                cityEntity,
                                place.getFormattedAddress(),
                                place.getLocation().getLatitude(),
                                place.getLocation().getLongitude(),
                                15, // Initial and standard time
                                false,
                                "",
                                new ArrayList<>()
                        );

                        spots.add(spot);
                    } else System.out.println("Spot " + place.getName() + " is already in the DB");
                }

                spotRepository.saveAll(spots);

                System.out.println("Places were saved satisfactorily in DB");

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

    public List<Spot> getUnValidatedSpotByCity(City city) {
        return spotRepository.getSpotByValidatedAndCity(false, city);
    }

    public List<Spot> getAllSpotByCity(City city) {
        return spotRepository.getSpotByCity(city);
    }

    public List<String> getDestinationSpots(Spot origin, List<Spot> destination) {
        return destination.stream()
                .filter(s -> !s.equals(origin))  //It only maintains the spots which not match with the origin
                .map(Spot::getName)  // Extract the names
                .collect(Collectors.toList()); // Convert to a list
    }


}
