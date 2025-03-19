package com.turismea.service;

import com.turismea.model.dto.placesDTO.Location;
import com.turismea.model.dto.routesDTO.*;
import com.turismea.model.entity.Spot;
import com.turismea.security.GoogleAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleRoutesService {

    private final WebClient webClient;
    private final SpotService spotService;
    private GoogleRouteResponse googleRouteResponse;

    @Value("${google.api.key}")
    private String API_KEY;


    public GoogleRoutesService(WebClient webClient, GoogleAuthService googleAuthService, SpotService spotService) {

        this.webClient = webClient;
        this.spotService = spotService;
    }



    /*
    * PROBLEMA-> Google no nos devuelve ni el nombre ni las coordenadas de los elementos (origen | destino), por lo que
    * tenemos que hallar la manera de asignarle un identificador a cada GoogleRouteResponse.
    *
    * Esto será necesario luego para poder recuperar esos spots y asignarlos a sus respectivos cityDistances.
    *
    * (1) ->  La lista de origenes tiene ids del 0->N, igual que la de destinos, ¿Como lo hacemos?
    * */

    public Flux<GoogleRouteResponse> getDistanceMatrix(List<Spot> spotList){

        //toList -> (JAVA 16+)  return an immutable list however, collect(Collectors.toList()) return an mutable list
        Flux<WayPoint> fluxListOfSpots = Flux.fromIterable(spotList.stream().map(spot ->
                new WayPoint(spot.getName(), new LocationWayPoint(new LatLng(spot.getLatitude(), spot.getLongitude())))
                ).toList());

        return fluxListOfSpots.flatMap(wayPoint -> {
                    Flux<WayPoint> wayPoints = spotService.getDestinationSpots(wayPoint, fluxListOfSpots);

                    return wayPoints.collectList().flatMap(destinations  -> {
                        //Only one element in the list due to google wait a list not an element
                        List<WayPoint> origin = List.of(wayPoint);
                        List<WayPoint> destinationList = destinations.stream().toList();

                        RouteRequestDTO routeRequestDTO = new RouteRequestDTO(origin, destinations);

                        return webClient.post().uri("/v2:computeRouteMatrix")
                                .header("Content-Type", "application/json")
                                .header("X-Goog-Api-Key", API_KEY)
                                .header("X-Goog-FieldMask", "originIndex,destinationIndex" +
                                        ",duration,distanceMeters")
                                .bodyValue(routeRequestDTO)
                                .retrieve()
                                .bodyToMono(GoogleRouteResponse.class);
                    });
                }
        );
    }
}
