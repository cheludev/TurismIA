package com.turismea.service;

import com.turismea.exception.*;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.*;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final CityService cityService;
    private final CityDistanceService cityDistanceService;
    @Autowired
    private WKTService wktService;
    @Autowired
    private TouristRepository touristRepository;


    public RouteService(RouteRepository routeRepository, TouristService touristService, CityService cityService,
                        OpenStreetMapService openStreetMapService, WKTService wktService,
                        RouteGeneratorService routeGeneratorService,
                        CityDistanceService cityDistanceService) {
        this.routeRepository = routeRepository;

        this.cityService = cityService;
        this.cityDistanceService = cityDistanceService;
    }



    public List<Route> saveRoute(Long touristId, Route route) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        route.setOwner(tourist);

        routeRepository.save(route);

        return routeRepository.getRoutesByOwner(tourist);
    }


    public List<Route> getSavedRoutes(Long touristId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));
        return routeRepository.getRouteByOwner(tourist);
    }

    public List<Route> getRoutesByCity(String city) {
        City cityAux = cityService.findByName(city).orElseThrow(() -> new CityNotFoundException(city));
        return routeRepository.getRoutesByCity(cityAux);
    }

    public List<Route> getRoutesByOwner(Long ownerId) {
        return routeRepository.findRouteByOwner_Id(ownerId);
    }

    public void deleteRoute(Long routeId) {
        routeRepository.deleteById(routeId);
    }
    public Route getRouteWithSpotsById(Long routeId) {
        return routeRepository.findWithSpotsById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public void delete(Route route) {
        routeRepository.delete(route);
    }

    public double calculateRatingOfARoute(List<Spot> spots){
        if (spots == null || spots.isEmpty()) return 0.0;
        return spots.stream().mapToDouble(Spot::getRating).sum()/spots.size();
    }


    public int calculateDurationOfARoute(List<Spot> spots) {

        if (spots.size() < 2) {
            return spots.get(0).getAverageTime();
        }

        int duration = 0;

        for (int i = 0; i < spots.size() - 1; i++) {
            Spot spotA = spots.get(i);
            Spot spotB = spots.get(i + 1);

            duration += cityDistanceService.getDurationBetween(
                    new LocationDTO(spotA.getLatitude(), spotA.getLongitude()),
                    new LocationDTO(spotB.getLatitude(), spotB.getLongitude())
            );

            duration += spotA.getAverageTime();
        }

        return duration;
    }


    public Route save(Route route) {
        return routeRepository.save(route);
    }


    public List<Route> getDraftsOfAnUser(Long id) {
        return routeRepository.findByOwner_IdAndDraft(id, true);
    }
}
