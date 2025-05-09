package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.*;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.OsrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.*;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.model.dto.RouteDTO.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
    @Autowired
    private RoutePathService routePathService;

    @Autowired
    private OpenStreetMapService openStreetMapService;

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
        return routeRepository.getSavedDraftRoutesByOwnerFetchSpots(tourist);
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

    public List<Route> getRoutesByCityId(Long cityId) {
        return routeRepository.findByCityId(cityId);
    }

    public List<Route> getRoutesByCityName(String cityName) {
        return routeRepository.findByCity_NameIgnoreCase(cityName);
    }

    public List<LocationDTO> buildPolyline(Route route) {
        List<LocationDTO> polyline = new ArrayList<>();

        List<Spot> spots = route.getSpots();
        if (spots.size() < 2) return polyline;

        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < spots.size() - 1; i++) {
            Spot from = spots.get(i);
            Spot to = spots.get(i + 1);

            List<CityDistance> distances = cityDistanceService.getListOfCityDistancesIgnoringOrder(from, to);

            CityDistance distance = null;

            if (!distances.isEmpty()) {
                distance = distances.get(0);
            } else {
                // Si no existe CityDistance, no podemos hacer nada (tendría que crearse primero)
                System.err.println("❗ No CityDistance entre " + from.getName() + " y " + to.getName());
                continue;
            }

            String geometryJson = distance.getGeometryJson();

            if (geometryJson != null) {
                // ✅ Ya hay geometry guardada, la usamos
                try {
                    com.turismea.model.dto.RouteDTO.Geometry geometry = objectMapper.readValue(
                            geometryJson, com.turismea.model.dto.RouteDTO.Geometry.class
                    );

                    if (geometry != null && geometry.getCoordinates() != null) {
                        for (List<Double> coord : geometry.getCoordinates()) {
                            polyline.add(new LocationDTO(coord.get(1), coord.get(0)));
                        }
                    }

                } catch (Exception e) {
                    System.err.println("❗ Error deserializando geometry para " + from.getName() + " -> " + to.getName() + ": " + e.getMessage());
                }

            } else {
                // ⚠ No hay geometry → la calculamos ahora y la guardamos

                System.out.println("ℹ No había geometry guardada. Calculando con OSRM para " + from.getName() + " -> " + to.getName());

                List<RouteDTO> routes = openStreetMapService.getDistance(
                        new LocationDTO(from.getLatitude(), from.getLongitude()),
                        new LocationDTO(to.getLatitude(), to.getLongitude())
                ).block();

                if (routes != null && !routes.isEmpty()) {
                    RouteDTO bestRoute = routes.stream()
                            .min(Comparator.comparingLong(RouteDTO::getDuration))
                            .get();

                    String newGeometryJson = null;
                    try {
                        newGeometryJson = objectMapper.writeValueAsString(bestRoute.getGeometry());
                        // Guardamos en la CityDistance para que no se recalculen en el futuro
                        distance.setGeometryJson(newGeometryJson);
                        cityDistanceService.save(distance);

                        System.out.println("✅ Geometry guardada en la CityDistance entre " + from.getName() + " -> " + to.getName());

                        // Ahora añadimos las coordenadas al polyline
                        bestRoute.getGeometry().getCoordinates().forEach(coord -> {
                            polyline.add(new LocationDTO(coord.get(1), coord.get(0)));
                        });

                    } catch (Exception e) {
                        System.err.println("❗ Error serializando y guardando geometry para " + from.getName() + " -> " + to.getName());
                    }
                } else {
                    System.err.println("❗ OSRM no pudo devolver ruta entre " + from.getName() + " y " + to.getName());
                }
            }
        }

        return polyline;
    }





}
