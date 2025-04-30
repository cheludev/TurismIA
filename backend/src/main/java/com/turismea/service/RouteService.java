package com.turismea.service;

import com.turismea.exception.*;
import com.turismea.exception.NotTheOwnerOfRouteException;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.*;
import com.turismea.repository.RouteRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final TouristService touristService;
    private final CityService cityService;
    private final RouteGeneratorService routeGeneratorService;


    public RouteService(RouteRepository routeRepository, TouristService touristService, CityService cityService, CityDistanceService cityDistanceService, OpenStreetMapService openStreetMapService, WKTService wktService, RouteGeneratorService routeGeneratorService) {
        this.routeRepository = routeRepository;

        this.touristService = touristService;
        this.cityService = cityService;
        this.routeGeneratorService = routeGeneratorService;
    }

    public Mono<Void> addSpotToRoute(Route route, Spot newSpot, long travelTime, boolean last) {
        List<Spot> spots = route.getSpots();

        if (spots.isEmpty() || last) {
            spots.add(newSpot);
            System.out.println(last? "⭕⬅\uFE0F Last" : "⭕⬅\uFE0F First" + " spot added" +
                    " | Total route duration: " + route.getDuration() + "s");
            return Mono.empty();
        }

        Spot previousSpot = spots.get(spots.size() - 1);

        long newDuration = route.getDuration() + travelTime + newSpot.getAverageTime();
        route.setDuration(newDuration);
        spots.add(newSpot);

        System.out.println("✅ Spot added: " + newSpot.getName() +
                "\n   ├─ From: " + previousSpot.getName() +
                "\n   ├─ Travel time: " + travelTime + "s" +
                "\n   ├─ Spot avg. visit time: " + newSpot.getAverageTime() + "s" +
                "\n   └─ New total route duration: " + newDuration + "s");

        return Mono.empty();
    }


    public Route editRoute(Route ogRoute, Route newRoute, Long touristId) throws Exception {

        if(newRoute.getSpots().isEmpty() || ogRoute.getSpots().isEmpty()) {
            throw new Exception("The route can not has zero spots");
        }

        if(touristId == null || touristService.findById(touristId).isEmpty()) {
            throw new UserNotFoundException(touristId);
        }

        // We have to calc the new route duration of the route

        return routeGeneratorService.createRoute(newRoute);
    }
    public List<Route> saveRoute(Long touristId, Route route) {
        Tourist tourist = touristService.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        route.setOwner(tourist);

        routeRepository.save(route);

        return routeRepository.getRoutesByOwner(tourist);
    }


    public List<Route> getSavedRoutes(Long touristId) {
        Tourist tourist = touristService.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));
        return routeRepository.getRouteByOwner(tourist);
    }

    public List<Route> getRoutesByCity(String city) {
        Optional<City> cityAux = cityService.findByName(city).orElseThrow(() -> new CityNotFoundException(city));
        return routeRepository.getRoutesByCity(cityAux.orElseGet(() -> cityService.existOrCreateCity(city)));
    }

    public List<Route> getRoutesByOwner(Long ownerId) {
        return routeRepository.findRouteByOwner_Id(ownerId);
    }

    public void deleteRoute(Long routeId) {
        routeRepository.deleteById(routeId);
    }

    public Route getRouteById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public void delete(Route route) {
        routeRepository.delete(route);
    }
}
