package com.turismea.service;

import com.turismea.exception.*;
import com.turismea.exception.NotTheOwnerOfRouteException;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final TouristService touristService;
    private final CityService cityService;


    public RouteService(RouteRepository routeRepository, TouristService touristService, CityService cityService) {
        this.routeRepository = routeRepository;

        this.touristService = touristService;
        this.cityService = cityService;
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }



    public Route editRoute(Long originalRouteId, Route newRoute, Long touristId) {
        Route OGRoute = routeRepository.findById(originalRouteId)
                .orElseThrow(() -> new RouteNotFoundException(originalRouteId));

        if (!OGRoute.getOwner().getId().equals(touristId)) {
            throw new NotTheOwnerOfRouteException(touristId, originalRouteId);
        }


        OGRoute.setName(newRoute.getName());
        OGRoute.setCity(newRoute.getCity());
        OGRoute.setOwner(newRoute.getOwner());
        OGRoute.setRate(newRoute.getRate());
        OGRoute.setSpots(newRoute.getSpots());
        OGRoute.setDescription(newRoute.getDescription());

        return routeRepository.save(OGRoute);
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
        City cityAux = cityService.findByName(city).orElseThrow(() -> new CityNotFoundException(city));
        return routeRepository.getRoutesByCity(cityAux);
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
