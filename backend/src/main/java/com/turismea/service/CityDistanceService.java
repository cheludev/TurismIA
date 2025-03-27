package com.turismea.service;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
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

@Service
public class CityDistanceService {

    private final CityDistanceRepository cityDistanceRepository;
    private final OpenStreetMapService openStreetMapService;
    private final RouteRepository routeRepository;

    @Autowired
    public CityDistanceService(CityDistanceRepository cityDistanceRepository, OpenStreetMapService openStreetMapService,
                               RouteRepository routeRepository) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.openStreetMapService = openStreetMapService;
        this.routeRepository = routeRepository;
    }

    public void saveCityDistances(List<CityDistance> spotDistancesList) {
        cityDistanceRepository.saveAll(spotDistancesList);
    }

    @Transactional
    public CityDistance save(CityDistance cityDistance) {
        return cityDistanceRepository.save(cityDistance);
    }


    @Transactional
    public void getAllDistances(City city, List<Spot> spotList) {
        if (spotList == null || spotList.size() < 2) {
            System.err.println("Spot list is null or it has not sufficient elements.");
            return;
        }

        for (int i = 0; i < spotList.size(); i++) {
            Spot spotA = spotList.get(i);

            for (int j = i + 1; j < spotList.size(); j++) {
                Spot spotB = spotList.get(j);

                openStreetMapService.getDistance(
                                new LocationDTO(spotA.getLatitude(), spotA.getLongitude()),
                                new LocationDTO(spotB.getLatitude(), spotB.getLongitude())
                        )
                        .doOnError(e -> System.err.println("Error getting distance between " + spotA.getName() + " and " + spotB.getName() + ": " + e.getMessage()))
                        .subscribe(routeList -> {
                            if (routeList == null || routeList.isEmpty()) {
                                System.err.println("Routes not found between -> " + spotA.getName() + " and " + spotB.getName());
                                return;
                            }

                            try {
                                RouteDTO bestRoute = routeList.stream()
                                        .min(Comparator.comparingLong(RouteDTO::getDuration))
                                        .get();

                                save(new CityDistance(
                                        city,
                                        spotA,
                                        spotB,
                                        bestRoute.getDistance(),
                                        bestRoute.getDuration()
                                ));

                            } catch (Exception e) {
                                System.err.println("Error saving " + spotA.getName() + " and " + spotB.getName() + ": " + e.getMessage());
                            }
                        });
            }
        }
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
                    System.err.println("Error: " + e.getMessage());
                    return Mono.empty();
                })
                .block();
    }


}
