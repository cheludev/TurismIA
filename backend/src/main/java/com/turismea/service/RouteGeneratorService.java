package com.turismea.service;

import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.SpotNotFoundException;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteGeneratorService {

    private final SpotService spotService;
    private final OrdenationAlgorithimService ordenationAlgorithimService;
    private final CityDistanceService cityDistanceService;
    private final RouteService routeService;


    private final Map<Spot, List<CityDistance>> connectionsCache = new HashMap<>();

    public RouteGeneratorService(SpotService spotService, OrdenationAlgorithimService ordenationAlgorithimService,
                                 CityDistanceService cityDistanceService, RouteService routeService) {
        this.spotService = spotService;
        this.ordenationAlgorithimService = ordenationAlgorithimService;
        this.cityDistanceService = cityDistanceService;
        this.routeService = routeService;
    }

    public Route generateRoute(LocationDTO initialPoint, LocationDTO finalPoint, int secTime) {
        Spot initialSpot, finalSpot;

        String wktPointA = String.format(Locale.US, "POINT(%f %f)", initialPoint.getLongitude(), initialPoint.getLatitude());
        String wktPointB = String.format(Locale.US, "POINT(%f %f)", finalPoint.getLongitude(), finalPoint.getLatitude());
        double maxRad = spotService.getDistanceBetween(wktPointA, wktPointB);

        double INITIAL_RADIUS = 10.0;
        initialSpot = getBetterNearestPoint(initialPoint, INITIAL_RADIUS, maxRad);
        System.out.println("PUNTO INICIAL SELECCIONADO " + initialSpot.getName());
        finalSpot = getBetterNearestPoint(finalPoint, INITIAL_RADIUS, maxRad);
        System.out.println("PUNTO Final SELECCIONADO " + finalSpot.getName());

        List<Route> listOfRoutes = getRoutes(secTime, initialSpot, finalSpot, initialPoint, finalPoint);

        if (listOfRoutes.isEmpty()) {
            throw new SpotNotFoundException("No valid route found with given constraints.");
        }
        return listOfRoutes.get(0);
    }

    private List<Route> getRoutes(int secTime, Spot initialSpot, Spot finalSpot, LocationDTO initialPoint,
                                  LocationDTO finalPoint) {
        if (initialSpot == null || finalSpot == null) {
            throw new SpotNotFoundException();
        }

        long durationBetween = cityDistanceService.getDurationBetween(initialPoint,finalPoint);
        System.out.println("La duracion entre el punto inicial y el final es: " + durationBetween);
        if(durationBetween >= secTime) {
            throw new RouteNotFoundException("Route time is less than the duration between initial point and destination");
        }

        List<Route> listOfRoutes = traverseTheSpotGraphToGetRoutes(initialSpot, finalSpot, secTime, initialPoint, finalPoint);
        listOfRoutes = getSortedListOfRoutes(listOfRoutes);
        return listOfRoutes;
    }

    public Route createRoute(Route route) {
        List<Spot> spots = route.getSpots();
        if (spots == null || spots.size() < 2) {
            throw new IllegalArgumentException("The route must contain at least two spots.");
        }

        long totalDuration = 0;

        for (int i = 1; i < spots.size(); i++) {
            Spot previous = spots.get(i - 1);
            Spot current = spots.get(i);

            // Get the duration between two spots from the database (CityDistance)
            List<CityDistance> distances = cityDistanceService.getListOfCityDistancesIgnoringOrder(previous, current);

            if (!distances.isEmpty()) {
                long travelDuration = distances.get(0).getDuration();
                totalDuration += travelDuration;
            } else {
                System.err.println("⚠️ No CityDistance found between " + previous.getName() + " and " + current.getName());
            }

            // Add the average visiting time of the current spot
            totalDuration += current.getAverageTime();
        }

        route.setDuration(totalDuration);
        return route;
    }


    private List<Route> traverseTheSpotGraphToGetRoutes(Spot initialSpot, Spot finalSpot, int time,
                                                        LocationDTO initialPoint, LocationDTO finalPoint) {
        List<Route> result = new ArrayList<>();
        Set<Spot> visited = new HashSet<>();
        Route actualRoute = new Route();
        actualRoute.setSpots(new LinkedList<>());

        dfs(initialSpot, finalSpot, actualRoute, visited, result, time, initialPoint, finalPoint);
        return result;
    }

    private void dfs(Spot current, Spot destiny, Route route, Set<Spot> visited, List<Route> result,
                     int durationMax, LocationDTO initialPoint, LocationDTO finalPoint) {

        if (visited.contains(current)) return;
        if (route.getDuration() > durationMax) return;

        visited.add(current);
        int originalSize = route.getSpots().size();
        long originalDuration = route.getDuration();

        try {
            if (route.getSpots().isEmpty()) { //It implies that is the first iteration
                // Add synthetic initial point only once at the start
                Spot initialSynthetic = spotService.getFinalOrInitialPoint(0, initialPoint);
                route.getSpots().add(initialSynthetic);

                // Calculate duration from synthetic initial point to first real spot
                long travelDuration = cityDistanceService.getDurationBetween(initialPoint,
                        new LocationDTO(current.getLatitude(), current.getLongitude()));
                // Add the spot to the route
                routeService.addSpotToRoute(route, current, travelDuration, current.equals(destiny)).block();
            } else { //For the rest of iteration
                Spot previousSpot = route.getSpots().get(route.getSpots().size() - 1);
                List<CityDistance> distances = cityDistanceService.getListOfCityDistancesIgnoringOrder(previousSpot, current);
                if (!distances.isEmpty()) {
                    long travelDuration = distances.get(0).getDuration();
                    routeService.addSpotToRoute(route, current, travelDuration, current.equals(destiny)).block();
                } else {
                    System.err.println("⚠️ No CityDistance found between " + previousSpot.getName() + " and " + current.getName());
                    visited.remove(current);
                    return;
                }
            }
        } catch (Exception e) {
            visited.remove(current);
            return;
        }

        if (current.equals(destiny)) {
            try {
                Spot previousSpot = route.getSpots().get(route.getSpots().size() - 1);
                Spot finalSynthetic = spotService.getFinalOrInitialPoint(1, finalPoint);

                long travelDuration = cityDistanceService.getDurationBetween(
                        new LocationDTO(previousSpot.getLatitude(), previousSpot.getLongitude()),
                        finalPoint
                );
                routeService.addSpotToRoute(route, finalSynthetic, travelDuration, current.equals(destiny)).block();

                // Final time validation after full route is constructed
                if (route.getDuration() > durationMax) {
                    System.out.println("⚠️ Skipping final route due to total duration overflow: " + route.getDuration() + "s > " + durationMax + "s");
                    route.getSpots().removeLast(); // Remove Final Point before backtracking
                    route.setDuration(originalDuration);
                    visited.remove(current);
                    return;
                }

                result.add(new Route(route));
            } catch (Exception e) {
                System.err.println("❌ Error adding Final Point: " + e.getMessage());
            }

        } else {
            List<CityDistance> connections = connectionsCache.computeIfAbsent(current,
                    cityDistanceService::getAllConnectionsOf);

            connections.sort(Comparator.comparingLong(CityDistance::getDuration));

            for (CityDistance connection : connections) {
                Spot neighbor = connection.getSpotA().equals(current) ? connection.getSpotB() : connection.getSpotA();
                long estimatedDuration = route.getDuration() + connection.getDuration() + neighbor.getAverageTime();

                if (estimatedDuration > durationMax) {
                    continue;
                }

                dfs(neighbor, destiny, route, visited, result, durationMax, initialPoint, finalPoint);
            }
        }

        visited.remove(current);
        route.setDuration(originalDuration);
        while (route.getSpots().size() > originalSize) {
            Spot removed = route.getSpots().removeLast();
            System.out.println("↩️ Backtracking: removing spot " + removed.getName());
        }
    }




    private List<Route> getSortedListOfRoutes(List<Route> listOfRoutes) {
        if (listOfRoutes.isEmpty()) {
            return new ArrayList<>();
        }

        listOfRoutes.sort(Comparator.comparingDouble(route ->
                (route.getRate() + route.getSpots().size()) / (double) route.getDuration()
        ));

        return listOfRoutes;
    }

    public Spot getBetterNearestPoint(LocationDTO locationDTO, double radius, double maxRadius) {
        System.out.println(locationDTO.getLatitude() + "," + locationDTO.getLongitude());
        String wktPoint = String.format(Locale.US, "POINT(%f %f)",
                locationDTO.getLongitude(), locationDTO.getLatitude());

        List<Spot> spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);

        while (spots.isEmpty() && radius < maxRadius) {
            radius += 10;
            spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);
        }

        if (spots.isEmpty()) {
            return null;
        }

        List<Long> durations = spots.stream()
                .map(destination -> {
                    Long duration = cityDistanceService.getDurationBetween(locationDTO,
                            new LocationDTO(destination.getLatitude(), destination.getLongitude()));
                    System.out.println("⏱️ Duration to " + destination.getName() + ": " + duration + "s");
                    return duration;
                })
                .toList();

        ordenationAlgorithimService.sortByDurationAndRating(spots, durations);
        return spots.get(0);
    }
}
