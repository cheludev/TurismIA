package com.turismea.service;

import com.turismea.configuration.WebClientLogging;
import com.turismea.model.dto.placesDTO.Location;
import com.turismea.model.dto.routesDTO.*;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import com.turismea.security.GoogleAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GoogleRoutesService {

    private final WebClient webClient;
    private final SpotService spotService;
    private final CityService cityService;
    private final CityDistanceService cityDistanceService;
    private GoogleRouteResponse googleRouteResponse;

    @Value("${google.api.key}")
    private String API_KEY;


    public GoogleRoutesService(@Qualifier("routesWebClient") WebClient webClient,
                               GoogleAuthService googleAuthService,
                               SpotService spotService,
                               CityService cityService,
                               @Lazy CityDistanceService cityDistanceService) {
        this.webClient = webClient;
        this.spotService = spotService;
        this.cityService = cityService;
        this.cityDistanceService = cityDistanceService;
    }





    /*
    * PROBLEMA -> Ya hace las peticiones a la API, pero no envia los indices ordenados, como lo hacemos?
    * */

    public Mono<Void> getDistanceMatrixSequential(List<Spot> spotList) {
        return Flux.fromIterable(spotList)
                .concatMap(originSpot -> { // Process each spot as an origin, sequentially

                    List<Spot> destinationSpots = spotList.stream()
                            .filter(dest -> !dest.equals(originSpot)) // Avoid calculating distance to itself
                            .toList();

                    if (destinationSpots.isEmpty()) {
                        return Mono.empty(); // No destinations available
                    }

                    // Create WayPoint objects for the request
                    List<Waypoint> originWayPoint = List.of(new Waypoint(originSpot.getName(),
                            new LocationWayPoint(new LatLng(new Coordinates(originSpot.getLatitude(), originSpot.getLongitude())))));

                    List<Waypoint> destinationWayPoints = destinationSpots.stream()
                            .map(dest -> new Waypoint(dest.getName(),
                                    new LocationWayPoint(new LatLng(new Coordinates(dest.getLatitude(), dest.getLongitude())))))
                            .toList();

                    RouteRequestDTO routeRequestDTO = new RouteRequestDTO(originWayPoint, destinationWayPoints);

                    return webClient.post()
                            .uri("/distanceMatrix/v2:computeRouteMatrix")
                            .header("Content-Type", "application/json")
                            .header("X-Goog-Api-Key", API_KEY)
                            .header("X-Goog-FieldMask", "originIndex,destinationIndex,distanceMeters,duration")
                            .bodyValue(routeRequestDTO)
                            .retrieve()
                            .bodyToFlux(GoogleRouteResponse.class)
                            .flatMap(response -> {

                                if (response.getDistanceMeters() == null) {
                                    System.out.println("⚠️ Warning: No distance or duration for originIndex "
                                            + response.getOriginIndex() + " and destinationIndex " + response.getDestinationIndex());
                                    return Mono.empty(); // Ignorar este caso
                                }
                                // Find the origin spot using coordinates
                                Spot actualOriginSpot = spotService.findByLatitudeAndLongitude(
                                        originWayPoint.get(0).getWaypoint().getLocation().getLatLng().getLatitude(),
                                        originWayPoint.get(0).getWaypoint().getLocation().getLatLng().getLongitude()
                                );

                                // Find the destination spot using coordinates
                                Spot actualDestinationSpot = spotService.findByLatitudeAndLongitude(
                                        destinationWayPoints.get(response.getDestinationIndex()).getWaypoint().getLocation().getLatLng().getLatitude(),
                                        destinationWayPoints.get(response.getDestinationIndex()).getWaypoint().getLocation().getLatLng().getLongitude()
                                );

                                // Create a CityDistance object with the correct spots
                                CityDistance cityDistance = new CityDistance(
                                        actualOriginSpot.getCity(),
                                        actualOriginSpot,
                                        actualDestinationSpot,
                                        response.getDistanceMeters(),
                                        Integer.parseInt(response.getDuration().replace("s", ""))
                                );

                                // Save the distance using Mono.fromRunnable to ensure execution within the reactive flow
                                return Mono.fromRunnable(() -> cityDistanceService.save(cityDistance));
                            })
                            .then();
                })
                .then();
    }


    }
