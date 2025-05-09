package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.PlacesDTO.GooglePlacesResponse;
import com.turismea.model.dto.PlacesDTO.Place;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityRepository;
import com.turismea.repository.SpotRepository;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final GoogleSpotService googleSpotService;
    private final CityService cityService;
    private final CityRepository cityRepository;

    public SpotService(SpotRepository spotRepository, GoogleSpotService googleSpotService, CityService cityService, CityRepository cityRepository) {
        this.spotRepository = spotRepository;
        this.googleSpotService = googleSpotService;
        this.cityService = cityService;
        this.cityRepository = cityRepository;
    }

    @Transactional
    public void validateSpot(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new SpotNotFoundException(spotId));

        if (!spot.isValidated()) {
            spot.setValidated(true);
        }
    }

    public List<Spot> getSpotsByCity(City city) {
        return spotRepository.findByCity(city);
    }

    public List<Spot> getSpotsByCityName(String cityName) {
        Optional<City> city = cityRepository.findByName(cityName);
        if(city.isPresent()){
            return spotRepository.findByCity(city.get());
        } else {
            return new ArrayList<>();
        }
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
                        //Mapping de json response
                        ObjectMapper objectMapper = new ObjectMapper();
                        GooglePlacesResponse response = objectMapper.readValue(jsonResponse, GooglePlacesResponse.class);

                        List<Place> places = response.getPlaces();

                        City cityEntity = cityService.existOrCreateCity(city);

                        //Getting the existing spots in order to avoid duplicates
                        List<String> existingNames = spotRepository.findAllNames();

                        List<Spot> spots = places.stream()
                                //Filtering to avoid duplicates
                                .filter(place -> !existingNames.contains(place.getName()))
                                .map(place -> {
                                    Coordinate coordinate = new Coordinate(place.getLocationDTO().getLatitude(),
                                            place.getLocationDTO().getLongitude());
                                    //We cant save a lat and long like coordinates using "Point"
                                    Point point = geometryFactory.createPoint(coordinate);
                                    return new Spot(
                                            place.getName(),
                                            cityEntity,
                                            place.getFormattedAddress(),
                                            place.getLocationDTO().getLatitude(),
                                            place.getLocationDTO().getLongitude(),
                                            900,
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

    public List<Spot> getValidatedSpotByCity(String city) {
        Optional<City> cityC = cityService.findByName(city);

        if(cityC.isPresent()) {
            return spotRepository.getSpotByValidatedAndCity(true, cityC.get());
        } else {
            throw new SpotNotFoundException();
        }
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
        System.out.println("===> WKT A: " + wktPointA);
        System.out.println("===> WKT B: " + wktPointB);

        return spotRepository.getDistanceBetween(wktPointA, wktPointB);
    }

    public Spot getFinalOrInitialPoint(int type, LocationDTO locationDTO){
        if(locationDTO == null) {
            return new Spot();
        }
        String finalOrInitial;
        if (type == 0) { //initial
            finalOrInitial = "Initial Point";
        } else {
            finalOrInitial = "Final Point";
        }
        return new Spot(finalOrInitial, locationDTO.getLatitude(), locationDTO.getLongitude());
    }


    public Optional<Spot> findById(Long id) {
        return spotRepository.findById(id);
    }

    public boolean exitsById(Long id) {
        return spotRepository.existsById(id);
    }

    public List<Spot> findAllById(List<Long> spotIds) {
        return spotRepository.findAllById(spotIds);
    }

}
