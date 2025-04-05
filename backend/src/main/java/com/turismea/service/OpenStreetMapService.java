package com.turismea.service;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.osrmDistanceDTO.OsrmResponse;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OpenStreetMapService {

    private final WebClient webClient;

    public OpenStreetMapService(@Qualifier(value = "osrmWebClient") WebClient webClient) {

        this.webClient = webClient;
    }

    /**
     * Get the distances between two locations provided.
     * @param initialPoint Initial location
     * @param finalPoint Destination point
     */

    public Mono<List<RouteDTO>> getDistance(LocationDTO initialPoint, LocationDTO finalPoint){

        return webClient.get()
                .uri("/walking/" + initialPoint.getLongitude() + "," + initialPoint.getLatitude()
                        + ";" + finalPoint.getLongitude() + "," + finalPoint.getLatitude()
                        + "?overview=full&geometries=geojson&alternatives=true")
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .map(OsrmResponse::getRoutes);
    }

    public Mono<List<RouteDTO>> getRouteTime(LocationDTO initialPoint, LocationDTO finalPoint){

        return webClient.get()
                .uri("/walking/" + initialPoint.getLongitude() + "," + initialPoint.getLatitude()
                        + ";" + finalPoint.getLongitude() + "," + finalPoint.getLatitude()
                        + "?overview=full&geometries=geojson&alternatives=true")
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .map(OsrmResponse::getRoutes);
    }
}
