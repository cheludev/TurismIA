package com.turismea.service;

import com.turismea.exception.*;
import com.turismea.exception.NotTheOwnerOfRouteException;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.*;
import com.turismea.repository.RouteRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final TouristService touristService;
    private final CityService cityService;
    private final CityDistanceService cityDistanceService;
    private final OpenStreetMapService openStreetMapService;


    public RouteService(RouteRepository routeRepository, TouristService touristService, CityService cityService, CityDistanceService cityDistanceService, OpenStreetMapService openStreetMapService) {
        this.routeRepository = routeRepository;

        this.touristService = touristService;
        this.cityService = cityService;
        this.cityDistanceService = cityDistanceService;
        this.openStreetMapService = openStreetMapService;
    }
    public Mono<Void> addSpotToRoute(Route route, Spot newSpot) {
        List<Spot> spots = route.getSpots();

        // If it's the first spot, just add averageTime
        if (spots.isEmpty()) {
            route.setDuration(newSpot.getAverageTime());
            spots.add(newSpot);
            System.out.println("First spot added: " + newSpot.getName() + " | Duration: " + newSpot.getAverageTime() + "s");
            return Mono.empty();
        }

        Spot previousSpot = spots.get(spots.size() - 1);

        // Try to find CityDistance among the spots
        List<CityDistance> cityDistances = cityDistanceService.getListOfCityDistancesIgnoringOrder(previousSpot, newSpot);

        if (!cityDistances.isEmpty()) {
            long travelTime = cityDistances.get(0).getDuration();
            long newDuration = route.getDuration() + travelTime + newSpot.getAverageTime();
            route.setDuration(newDuration);
            spots.add(newSpot);

            System.out.println("Spot added: " + newSpot.getName() + " | Duration from " + previousSpot.getName() + ": " + travelTime + "s + Average time: " + newSpot.getAverageTime() + "s | Total route duration: " + newDuration + "s");
            return Mono.empty();
        }

        // If it is not exist CityDistance, we use OSM through cityDistanceService
        return Mono.fromCallable(() -> cityDistanceService.getDurationBetween(
                        new LocationDTO(previousSpot.getLatitude(), previousSpot.getLongitude()),
                        new LocationDTO(newSpot.getLatitude(), newSpot.getLongitude())))
                .doOnError(e -> System.err.println("❌ Error getting OSM duration: " + e.getMessage()))
                .flatMap(travelTime -> {
                    if (travelTime == null) {
                        System.err.println("⚠️ No duration found between: " + previousSpot.getName() + " and " + newSpot.getName());
                        return Mono.empty();
                    }

                    long newDuration = route.getDuration() + travelTime + newSpot.getAverageTime();
                    route.setDuration(newDuration);
                    spots.add(newSpot);

                    System.out.println("Spot added (OSM): " + newSpot.getName() +
                            " | Duration from " + previousSpot.getName() + ": " + travelTime + "s" +
                            " + Average time: " + newSpot.getAverageTime() + "s" +
                            " | Total route duration: " + newDuration + "s");

                    return Mono.empty();
                });
    }
    public Mono<Void> addSpotToRoute(Route route, Spot newSpot, long travelTime) {
        List<Spot> spots = route.getSpots();

        if (spots.isEmpty()) {
            route.setDuration(newSpot.getAverageTime());
            spots.add(newSpot);
            System.out.println("✅ First spot added: " + newSpot.getName() +
                    " | Avg. time: " + newSpot.getAverageTime() + "s" +
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
