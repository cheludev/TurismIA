package com.turismea.service;

import com.turismea.model.dto.Location;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.City;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityDistanceRepository;
import com.turismea.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public CityDistance save(CityDistance cityDistance) {
        return cityDistanceRepository.save(cityDistance);

    }



    public void getAllDistances(City city, List<Spot> spotList) {
        if (spotList == null || spotList.size() < 2) {
            System.err.println("La lista de spots es nula o no tiene suficientes elementos.");
            return;
        }

        for (int i = 0; i < spotList.size(); i++) {
            Spot spotA = spotList.get(i);

            for (int j = i + 1; j < spotList.size(); j++) {
                Spot spotB = spotList.get(j);

                openStreetMapService.getDistance(
                                new Location(spotA.getLatitude(), spotA.getLongitude()),
                                new Location(spotB.getLatitude(), spotB.getLongitude())
                        )
                        .doOnError(e -> System.err.println("Error al obtener distancia entre " + spotA.getName() + " y " + spotB.getName() + ": " + e.getMessage()))
                        .subscribe(routeList -> {
                            if (routeList == null || routeList.isEmpty()) {
                                System.err.println("No se encontraron rutas entre " + spotA.getName() + " y " + spotB.getName());
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
                                System.err.println("Error al guardar distancia entre " + spotA.getName() + " y " + spotB.getName() + ": " + e.getMessage());
                            }
                        });
            }
        }
    }

}
