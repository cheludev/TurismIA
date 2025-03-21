package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.model.dto.routesDTO.GoogleRouteResponse;
import com.turismea.model.dto.routesDTO.LocationWayPoint;
import com.turismea.model.dto.routesDTO.LatLng;
import com.turismea.model.dto.routesDTO.RouteRequestDTO;
import com.turismea.model.dto.routesDTO.Waypoint;
import com.turismea.model.dto.routesDTO.Status;
import com.turismea.model.dto.routesDTO.Coordinates;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import com.turismea.security.GoogleAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class GoogleRoutesService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GoogleRoutesService.class);
    private final WebClient webClient;
    private final SpotService spotService;
    private final CityService cityService;
    private final CityDistanceService cityDistanceService;

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

    public Mono<Void> getDistanceMatrixSequential(List<Spot> spotList) {
        return Flux.fromIterable(spotList)
                .concatMap(originSpot -> {
                    List<Spot> destinationSpots = spotList.stream()
                            .filter(dest -> !dest.equals(originSpot))
                            .toList();

                    if (destinationSpots.isEmpty()) return Mono.empty();

                    List<Waypoint> originWayPoint = List.of(new Waypoint(originSpot.getName(),
                            new LocationWayPoint(new LatLng(new Coordinates(originSpot.getLatitude(),
                                    originSpot.getLongitude())))));

                    List<Waypoint> destinationWayPoints = destinationSpots.stream()
                            .map(dest -> new Waypoint(dest.getName(),
                                    new LocationWayPoint(new LatLng(new Coordinates(dest.getLatitude(),
                                            dest.getLongitude())))))
                            .toList();

                    Map<Integer, Waypoint> destinationMap = new HashMap<>();
                    IntStream.range(0, destinationWayPoints.size())
                            .forEach(i -> destinationMap.put(i, destinationWayPoints.get(i)));

                    RouteRequestDTO routeRequestDTO = new RouteRequestDTO(originWayPoint, destinationWayPoints);
                    return webClient.post()
                            .uri("https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix")
                            .header("X-Goog-Api-Key", API_KEY)
                            .header("X-Goog-FieldMask", "originIndex,destinationIndex,distanceMeters,duration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(routeRequestDTO)
                            .retrieve()
                            .bodyToFlux(GoogleRouteResponse.class)
                            .doOnSubscribe(s -> log.info("📥 Suscrito a la respuesta de Google"))
                            .doOnNext(response -> log.info("📌 Recibido: {} -> {} [{}m, {}s]",
                                    response.getOriginIndex(), response.getDestinationIndex(),
                                    response.getDistanceMeters(), response.getDuration()))
                            .doOnComplete(() -> log.info("✅ Procesamiento de respuestas completado"))
                            .doOnError(e -> log.error("❌ Error en la respuesta de Google: {}", e.getMessage()))
                            .switchIfEmpty(Mono.fromRunnable(() -> log.warn("⚠️ No hay respuestas de Google, el Flux está vacío")))
                            .flatMap(response -> {
                                // Asegurar que duration sea final o efectivamente final
                                final int duration;
                                if (response.getDuration() != null) {
                                    try {
                                        duration = Integer.parseInt(response.getDuration().replace("s", ""));
                                    } catch (NumberFormatException e) {
                                        log.warn("Formato de duración inesperado: {}", response.getDuration());
                                        return Mono.empty();
                                    }
                                } else {
                                    duration = 0;
                                }

                                int distance = (response.getDistanceMeters() != null) ? response.getDistanceMeters() : 0;

                                Optional<Spot> actualOriginSpot = spotService.findByLatitudeAndLongitude(
                                        originWayPoint.get(0).getWaypoint().getLocation().getLatLng().getLatitude(),
                                        originWayPoint.get(0).getWaypoint().getLocation().getLatLng().getLongitude()
                                );

                                Optional<Spot> actualDestinationSpot = spotService.findByLatitudeAndLongitude(
                                        destinationMap.get(response.getDestinationIndex()).getWaypoint().getLocation()
                                                .getLatLng().getLatitude(),
                                        destinationMap.get(response.getDestinationIndex()).getWaypoint().getLocation()
                                                .getLatLng().getLongitude()
                                );

                                if (actualOriginSpot.isEmpty() || actualDestinationSpot.isEmpty()) {
                                    log.warn("❌ No Spot found with those coordinates for indices: {} -> {}",
                                            response.getOriginIndex(), response.getDestinationIndex());
                                    return Mono.empty();
                                }

                                CityDistance cityDistance = new CityDistance(
                                        actualOriginSpot.get().getCity(),
                                        actualOriginSpot.get(),
                                        actualDestinationSpot.get(),
                                        distance,
                                        duration
                                );

                                return Mono.fromCallable(() -> {
                                    try {
                                        log.info("💾 Saving CityDistance: {} -> {} [{}m, {}s]",
                                                actualOriginSpot.get().getName(),
                                                actualDestinationSpot.get().getName(),
                                                distance, duration);
                                        return cityDistanceService.save(cityDistance);
                                    } catch (Exception e) {
                                        log.error("❌ Error saving CityDistance: {}", e.getMessage(), e);
                                        return null;
                                    }
                                });
                            })

                            .doOnTerminate(() -> log.info("🎯 Proceso finalizado"))
                            .then();

                })
                .then();
    }
}
