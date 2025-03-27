package com.turismea.service;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteGeneratorService {

    private final SpotService spotService;
    private final OrdenationAlgorithimService ordenationAlgorithimService;
    private final OpenStreetMapService openStreetMapService;
    private final CityDistanceService cityDistanceService;

    public RouteGeneratorService(SpotService spotService, OrdenationAlgorithimService ordenationAlgorithimService,
                                 OpenStreetMapService openStreetMapService, CityDistanceService cityDistanceService) {
        this.spotService = spotService;
        this.ordenationAlgorithimService = ordenationAlgorithimService;
        this.openStreetMapService = openStreetMapService;
        this.cityDistanceService = cityDistanceService;
    }

    public Route generateRoute(Spot initialSpot, Spot finalSpot) {
        return null;
    }


    /**
     * Get the better initial point of the route considering all the nearest saved spots, which are compared among them,
     * and are sorted by rating and distances to the location provided.
     *
     * @param locationDTO Client location.
     * @param radius Searching radius.
     * @param maxRadius Maximus radius that can be reach.
     */

    public Spot getBetterInitialPoint(LocationDTO locationDTO, double radius, double maxRadius) {
        String wktPoint = String.format("POINT(%f %f)", locationDTO.getLongitude(), locationDTO.getLatitude());

        List<Spot> spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);

        while (spots.isEmpty() && radius <= maxRadius) {
            radius += 50;
            spots = spotService.getNearbySpotsToFromAPoint(wktPoint, radius);
        }

        List<Long> distances = new ArrayList<>();
        if (spots.isEmpty()) {
            return null;
        } else {
            distances = spots.stream().map(destination -> cityDistanceService.getDistancesBetween(locationDTO,
                            new LocationDTO(destination.getLatitude(), destination.getLongitude())))
                    .toList();
        }

        ordenationAlgorithimService.sortByDurationAndRating(spots, distances);
        return spots.get(0);
    }

}
