package com.turismea.service;

import com.turismea.model.entity.Spot;
import com.turismea.security.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GoogleMatrixService {

    private final GoogleAuthService googleAuthService;
    private final WebClient webClient;
    private final SpotService spotService;

    @Autowired
    public GoogleMatrixService(GoogleAuthService googleAuthService, WebClient webClient, SpotService spotService) {
        this.googleAuthService = googleAuthService;
        this.webClient = webClient;
        this.spotService = spotService;
    }

    public void getDistancesOfVerifiedSpots(List<Spot> spotList) {
        googleAuthService.getAccessToken()
                .flatMapMany(accessToken -> Flux.fromIterable(spotList)
                        .flatMap(origin -> {
                            List<String> destinations = spotService.getDestinationSpots(origin, spotList);
                            if (destinations.isEmpty()) {
                                return Mono.empty();
                            }

                            return webClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                            .path("/maps/api/distancematrix/json")
                                            .queryParam("origins", origin.getName())
                                            .queryParam("destinations", String.join("|", destinations))
                                            .queryParam("units", "imperial")
                                            .queryParam("key", accessToken)
                                            .build()
                                    )
                                    .retrieve()
                                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                                        if (response.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                                            return Mono.error(new RuntimeException("Error 401: Invalid access token"));
                                        }
                                        return Mono.error(new RuntimeException("Error 4XX: Client error in Google Distance Matrix API"));
                                    })
                                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                                            Mono.error(new RuntimeException("Error 5XX: Server error in Google Distance Matrix API"))
                                    )
                                    .bodyToMono(String.class)
                                    .doOnSuccess(response -> {
                                        System.out.println("Google Matrix response: " + response);
                                    });
                        })
                )
                .subscribe();
    }

}
