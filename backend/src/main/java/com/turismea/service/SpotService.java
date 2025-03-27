package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.dto.placesDTO.GooglePlacesResponse;
import com.turismea.model.dto.placesDTO.Place;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.SpotRepository;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Mono<List<Spot>> saveCitySpots(String city) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return googleSpotService.getSpots(city)
                .flatMap(jsonResponse -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        GooglePlacesResponse response = objectMapper.readValue(jsonResponse, GooglePlacesResponse.class);
                        List<Place> places = response.getPlaces();

                        City cityEntity = cityService.existOrCreateCity(city);

                        List<String> existingNames = spotRepository.findAllNames();

                        List<Spot> spots = places.stream()
                                .filter(place -> !existingNames.contains(place.getName()))
                                .map(place -> {
                                    System.err.println("*********************"
                                            + place.getRating());

                                            System.err.println(place.getLocationDTO().getLatitude() + " " +
                                                    place.getLocationDTO().getLongitude());
                                    Coordinate coordinate = new Coordinate(place.getLocationDTO().getLatitude(),
                                            place.getLocationDTO().getLongitude());
                                    Point point = geometryFactory.createPoint(coordinate);
                                    return new Spot(
                                            place.getName(),
                                            cityEntity,
                                            place.getFormattedAddress(),
                                            place.getLocationDTO().getLatitude(),
                                            place.getLocationDTO().getLongitude(),
                                            15,
                                            false,
                                            "",
                                            new ArrayList<>(),
                                            place.getRating(),
                                            point
                                    );
                                }
                                )
                                .collect(Collectors.toList());

                        if (!spots.isEmpty()) {
                            spotRepository.saveAll(spots);
                            System.out.println("Places were saved satisfactorily in DB");
                        } else {
                            System.out.println("No new spots to save");
                        }

                        return Mono.just(spots);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(new RuntimeException("Error processing JSON", e));
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

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    public Optional<Spot> findByLatitudeAndLongitude(Double latitude, Double longitude){
        List<Spot> result = spotRepository.findByLatitudeAndLongitude(latitude, longitude);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Spot> getNearbySpotsToFromAPoint(String wktPoint, double radius){
        return spotRepository.getNearbySpotsToFromAPoint(wktPoint, radius);
    }

    public Double getDistanceBetween(String wktPointA, String wktPointB) {
        return spotRepository.getDistanceBetween(wktPointA, wktPointB);
    }

}
