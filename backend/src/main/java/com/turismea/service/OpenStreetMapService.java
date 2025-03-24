package com.turismea.service;

import com.turismea.model.dto.osrmDistanceDTO.Location;
import com.turismea.model.dto.osrmDistanceDTO.OsrmResponse;
import com.turismea.model.dto.osrmDistanceDTO.RouteDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OpenStreetMapService {

    private final WebClient webClient;

    public OpenStreetMapService(@Qualifier(value = "osrmWebClient") WebClient webClient) {

        this.webClient = webClient;
    }

    public Mono<List<RouteDTO>> getDistance(Location initialPoint, Location finalPoint){

        return webClient.get()
                .uri("/walking/" + initialPoint.getLongitude() + "," + initialPoint.getLatitude()
                        + ";" + finalPoint.getLongitude() + "," + finalPoint.getLatitude()
                        + "?overview=full&geometries=geojson&alternatives=true")
                .retrieve()
                .bodyToMono(OsrmResponse.class)
                .map(OsrmResponse::getRoutes);




    }
}
