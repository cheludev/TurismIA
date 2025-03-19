package com.turismea.service;

import com.turismea.security.GoogleAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleSpotService {

    private final WebClient webClient;
    private final GoogleAuthService googleAuthService;

    public GoogleSpotService(WebClient webClient, GoogleAuthService googleAuthService) {
        this.webClient = webClient;
        this.googleAuthService = googleAuthService;
    }

    public Mono<String> getSpots(String city) {
        return googleAuthService.getAccessToken()
                .flatMap(accessToken -> {
                    String requestBody = "{"
                            + "\"textQuery\": \"touristic points in " + city + "capital España\","
                            + "\"regionCode\": \"ES\","
                            + "\"languageCode\": \"es\""
                            + "}";

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/places:searchText")
                                    .queryParam("fields", "places.displayName,places.formattedAddress,places.location,places.types")
                                    .build()
                            )
                            .header("Authorization", "Bearer " + accessToken)
                            .header("Content-Type", "application/json")
                            .bodyValue(requestBody)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                                return response.bodyToMono(String.class)
                                        .flatMap(errorBody -> Mono.error(new RuntimeException("Error 4XX: " + errorBody)));
                            })
                            .onStatus(HttpStatusCode::is5xxServerError, response ->
                                    Mono.error(new RuntimeException("Error 5XX: Server error in the Google Places API"))
                            )
                            .bodyToMono(String.class);
                });
    }


}
