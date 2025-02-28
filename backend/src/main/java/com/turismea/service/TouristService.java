package com.turismea.service;

import com.turismea.model.Route;
import com.turismea.model.RouteType;
import com.turismea.model.Tourist;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private RouteRepository routeRepository;


    public TouristService(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    public List<Route> getSavedRoutes(Long touristId) {
        return touristRepository.findById(touristId)
                .map(Tourist::getSavedRoutes)
                .orElse(null);
    }

    public List<Route> getCreatedRoutes(Long touristId) {
        return touristRepository.findById(touristId) //Get the "optional" tourist, if it is not empty ->
                .map(Tourist::getCreatedRoutes) //Then try to get its routes and return it, but if any of them are empty
                .orElse(null); //Return null
    }


    public List<Route> saveRoute(Long id, Route route, RouteType type) { //Save both types of routes
        Tourist tourist = touristRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tourist not found"));

        route.setOwner(tourist);
        route.setType(type);
        tourist.getSavedRoutes().add(route);

        routeRepository.save(route);
        touristRepository.save(tourist);

        return touristRepository.getSavedRoutes(id);
    }


}
