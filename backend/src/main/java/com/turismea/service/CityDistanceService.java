package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.OsrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.City;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityDistanceRepository;
import com.turismea.repository.RouteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CityDistanceService {

    private final CityDistanceRepository cityDistanceRepository;
    private final OpenStreetMapService openStreetMapService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public CityDistanceService(CityDistanceRepository cityDistanceRepository, OpenStreetMapService openStreetMapService,
                               RouteRepository routeRepository) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.openStreetMapService = openStreetMapService;
    }

    public void saveCityDistances(List<CityDistance> spotDistancesList) {
        cityDistanceRepository.saveAll(spotDistancesList);
    }

    @Transactional
    public CityDistance save(CityDistance cityDistance) {
        Spot spotA = cityDistance.getSpotA();
        Spot spotB = cityDistance.getSpotB();

        if (spotA.getId() > spotB.getId()) {
            Spot temp = spotA;
            spotA = spotB;
            spotB = temp;
        }

        Optional<CityDistance> existing = cityDistanceRepository
                .findBySpotsIgnoreOrder(spotA, spotB)
                .stream().findFirst();

        if (existing.isPresent()) {
            CityDistance existingDistance = existing.get();
            // 👇 Si no tiene geometry, actualizarlo
            if (existingDistance.getGeometryJson() == null || existingDistance.getGeometryJson().isEmpty()) {
                existingDistance.setGeometryJson(cityDistance.getGeometryJson());
                cityDistanceRepository.save(existingDistance);
            }
            return existingDistance;
        }

        // Si no existe, guardar normalmente
        return cityDistanceRepository.save(cityDistance);
    }

    @Transactional
    public void getAllDistances(City city, List<Spot> spotList) {
        if (spotList == null || spotList.size() < 2) {
            return;
        }

        for (int i = 0; i < spotList.size(); i++) {
            Spot spotA = spotList.get(i);

            for (int j = i + 1; j < spotList.size(); j++) {
                Spot spotB = spotList.get(j);

                // Comprobar si ya existe y tiene geometry
                Optional<CityDistance> existing = cityDistanceRepository
                        .findBySpotsIgnoreOrder(spotA, spotB)
                        .stream().findFirst();

                if (existing.isPresent() && existing.get().getGeometryJson() != null) {
                    System.out.println("✅ Geometry ya existe para " + spotA.getName() + " -> " + spotB.getName());
                    continue; // Saltar esta pareja
                }

                try {
                    List<RouteDTO> routeList = openStreetMapService
                            .getDistance(
                                    new LocationDTO(spotA.getLatitude(), spotA.getLongitude()),
                                    new LocationDTO(spotB.getLatitude(), spotB.getLongitude())
                            )
                            .block();

                    if (routeList == null || routeList.isEmpty()) {
                        System.err.println("❌ No se encontró ruta para " + spotA.getName() + " -> " + spotB.getName());
                        continue;
                    }

                    RouteDTO bestRoute = routeList.stream()
                            .min(Comparator.comparingLong(RouteDTO::getDuration))
                            .orElseThrow();

                    String geometryJson = objectMapper.writeValueAsString(bestRoute.getGeometry());

                    save(new CityDistance(
                            city,
                            spotA,
                            spotB,
                            bestRoute.getDistance(),
                            bestRoute.getDuration(),
                            geometryJson
                    ));

                    System.out.println("✔ Guardada distancia + geometry para " + spotA.getName() + " -> " + spotB.getName());

                } catch (Exception e) {
                    System.err.println("⚠️ Error procesando " + spotA.getName() + " -> " + spotB.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    @Transactional
    public int fillMissingGeometries() {
        List<CityDistance> distancesWithoutGeometry = cityDistanceRepository.findAll().stream()
                .filter(cd -> cd.getGeometryJson() == null || cd.getGeometryJson().isEmpty())
                .toList();

        int updatedCount = 0;

        for (CityDistance cityDistance : distancesWithoutGeometry) {
            try {
                List<RouteDTO> routeList = openStreetMapService
                        .getDistance(
                                new LocationDTO(cityDistance.getSpotA().getLatitude(), cityDistance.getSpotA().getLongitude()),
                                new LocationDTO(cityDistance.getSpotB().getLatitude(), cityDistance.getSpotB().getLongitude())
                        )
                        .block();

                if (routeList == null || routeList.isEmpty()) {
                    System.err.println("❌ No route found for " + cityDistance.getSpotA().getName() + " -> " + cityDistance.getSpotB().getName());
                    continue;
                }

                RouteDTO bestRoute = routeList.stream()
                        .min(Comparator.comparingLong(RouteDTO::getDuration))
                        .orElseThrow();

                String geometryJson = objectMapper.writeValueAsString(bestRoute.getGeometry());

                cityDistance.setGeometryJson(geometryJson);
                cityDistanceRepository.save(cityDistance);

                System.out.println("✔ Geometry filled for " + cityDistance.getSpotA().getName() + " -> " + cityDistance.getSpotB().getName());
                updatedCount++;

            } catch (Exception e) {
                System.err.println("⚠️ Error updating geometry for " + cityDistance.getSpotA().getName() + " -> " + cityDistance.getSpotB().getName() + ": " + e.getMessage());
            }
        }

        return updatedCount;
    }


    public Long getDistancesBetween(LocationDTO locationA, LocationDTO locationB) {
        return openStreetMapService.getDistance(locationA, locationB)
                .flatMap(routeList ->
                        routeList.stream()
                                .min(Comparator.comparingLong(RouteDTO::getDuration))
                                .map(route -> Mono.just(route.getDistance()))
                                .orElseGet(Mono::empty)
                )
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .block();
    }

    public Long getDurationBetween(LocationDTO locationA, LocationDTO locationB) {
        return openStreetMapService.getDistance(locationA, locationB)
                .flatMap(routeList ->
                        routeList.stream()
                                .min(Comparator.comparingLong(RouteDTO::getDuration))
                                .map(route -> Mono.just(route.getDuration()))
                                .orElseGet(Mono::empty)
                )
                .onErrorResume(e -> {
                    return Mono.empty();
                })
                .block();
    }

    public List<Spot> getListOfConnections(Spot spotA){
        return cityDistanceRepository.getConnections(spotA);
    }

    public List<CityDistance> getListOfCityDistancesIgnoringOrder(Spot spot1, Spot spot2){
        return cityDistanceRepository.findBySpotsIgnoreOrder(spot1, spot2);
    }
    public List<CityDistance> getAllConnectionsOf(Spot spot) {
        return cityDistanceRepository.findAllConnectionsOf(spot);
    }


}
