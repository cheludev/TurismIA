package com.turismea.service;

import com.turismea.exception.NotTheOwnerOfRouteEception;
import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.*;
import com.turismea.repository.AdminRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private final AdminRepository adminRepository;
    private RouteRepository routeRepository;


    public TouristService(TouristRepository touristRepository, AdminRepository adminRepository) {
        this.touristRepository = touristRepository;
        this.adminRepository = adminRepository;
    }

    public List<Route> getSavedRoutes(Long touristId) {
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));
        return routeRepository.getRouteByOwner(tourist);
    }


    public List<Route> saveRoute(Long touristId, Route route, RouteType type) { //Save both types of routes
        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        route.setOwner(tourist);
        route.setType(type);
        tourist.getSavedRoutes().add(route);

        routeRepository.save(route);
        touristRepository.save(tourist);

        return routeRepository.getRouteByOwner(tourist);
    }

    public Route editRoute(Long originalRouteId, Route newRoute, Long touristId) {
        // Find the existing route by ID
        Route OGRoute = routeRepository.findById(originalRouteId)
                .orElseThrow(() -> new RouteNotFoundException(originalRouteId));

        // Check if the touristId is the owner of the route
        if (!OGRoute.getOwner().getId().equals(touristId)) {
            throw new NotTheOwnerOfRouteEception();
        }


            // Update route properties
            OGRoute.setName(newRoute.getName());
            OGRoute.setCity(newRoute.getCity());
            OGRoute.setOwner(newRoute.getOwner());
            OGRoute.setRate(newRoute.getRate());
            OGRoute.setLocations(newRoute.getLocations());
            OGRoute.setDescription(newRoute.getDescription());

            // Save the updated route
            return routeRepository.save(OGRoute);
    }


    public boolean applyToModerator(Long touristId) {

        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        if(!tourist.getRole().equals(Role.MODERATOR)) {
            Promotion promotion = new Promotion(tourist);
            adminRepository.getReportList(tourist.getId()).add(promotion);
            return adminRepository.getReportList(tourist.getId()).contains(promotion);
        }
        return false;
    }

}
