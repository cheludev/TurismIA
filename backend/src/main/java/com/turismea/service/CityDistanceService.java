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
import org.springframework.data.repository.query.Param;
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
        // Ensure spots are ordered by ID to prevent duplicate entries due to inverse spot order
        Spot spotA = cityDistance.getSpotA();
        Spot spotB = cityDistance.getSpotB();

        if (spotA.getId() > spotB.getId()) {
            Spot temp = spotA;
            spotA = spotB;
            spotB = temp;
        }

        // Check if a CityDistance already exists between these two spots (ignoring order)
        Optional<CityDistance> existing = cityDistanceRepository
                .findBySpotsIgnoreOrder(spotA, spotB)
                .stream().findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        // Create a new CityDistance entity with the correctly ordered spots
        CityDistance toSave = new CityDistance(
                cityDistance.getCity(),
                spotA,
                spotB,
                cityDistance.getDistance(),
                cityDistance.getDuration()
        );

        // Save to database
        CityDistance saved = cityDistanceRepository.save(toSave);


        return saved;
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

                openStreetMapService.getDistance(
                                new LocationDTO(spotA.getLatitude(), spotA.getLongitude()),
                                new LocationDTO(spotB.getLatitude(), spotB.getLongitude())
                        )
                        .doOnError(e -> System.err.println("Error getting distance between " + spotA.getName() + " and " + spotB.getName() + ": " + e.getMessage()))
                        .subscribe(routeList -> {
                            if (routeList == null || routeList.isEmpty()) {
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
