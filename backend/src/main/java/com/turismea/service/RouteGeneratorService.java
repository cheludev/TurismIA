package com.turismea.service;

import com.turismea.model.dto.Location;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteGeneratorService {

    private final SpotService spotService;
    private final OrdenationAlgorithimService ordenationAlgorithimService;

    public RouteGeneratorService(SpotService spotService, OrdenationAlgorithimService ordenationAlgorithimService) {
        this.spotService = spotService;
        this.ordenationAlgorithimService = ordenationAlgorithimService;
    }

    public Route generateRoute(Spot initialSpot, Spot finalSpot) {
        return null;
    }

    public Spot getBetterInitialPoint(Location location, double radius, double maxRadius) {
        String wktPoint = String.format("POINT(%f %f)", location.getLongitude(), location.getLatitude());

        List<Spot> spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);

        while (spots.isEmpty() && radius <= maxRadius) {
            radius += 50;
            spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);
        }

        if (spots.isEmpty()) return null;

        ordenationAlgorithimService.sortByRating(spots);
        return spots.get(0);
    }

}
