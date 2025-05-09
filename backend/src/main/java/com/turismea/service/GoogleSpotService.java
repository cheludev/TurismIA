package com.turismea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.turismea.model.dto.PlacesDTO.Point;
import com.turismea.security.GoogleAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleSpotService {

    private final WebClient webClient;
    private final GoogleAuthService googleAuthService;

    public GoogleSpotService(@Qualifier("placesWebClient") WebClient webClient, GoogleAuthService googleAuthService) {
        this.webClient = webClient;
        this.googleAuthService = googleAuthService;
    }

    public Mono<String> getSpots(String city) {
        return googleAuthService.getAccessToken()
                .flatMap(accessToken -> {
                    String requestBody = "{"
                            + "\"textQuery\": \"touristic points in metropolitan area of" + city + "capital España\","
                            + "\"regionCode\": \"ES\","
                            + "\"languageCode\": \"es\""
                            + "}";

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1/places:searchText")
                                    .queryParam("fields", "places.displayName,places.formattedAddress,places.location,places.types,places.rating")
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


    public Mono<Point> getSpotByName(String locationName) {
        return googleAuthService.getAccessToken()
                .flatMap(accessToken -> {
                    String requestBody = "{"
                            + "\"textQuery\": \"" + locationName + "\","
                            + "\"regionCode\": \"ES\","
                            + "\"languageCode\": \"es\""
                            + "}";

                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1/places:searchText")
                                    .queryParam("fields", "places.location")
                                    .build()
                            )
                            .header("Authorization", "Bearer " + accessToken)
                            .header("Content-Type", "application/json")
                            .bodyValue(requestBody)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException("Error 4XX: " + errorBody)))
                            )
                            .onStatus(HttpStatusCode::is5xxServerError, response ->
                                    Mono.error(new RuntimeException("Error 5XX: Server error in the Google Places API"))
                            )
                            .bodyToMono(JsonNode.class)
                            .flatMap(jsonNode -> {
                                if (jsonNode.has("places") && jsonNode.get("places").isArray() && jsonNode.get("places").size() > 0) {
                                    JsonNode location = jsonNode.get("places").get(0).get("location");
                                    double lat = location.get("latitude").asDouble();
                                    double lng = location.get("longitude").asDouble();
                                    return Mono.just(new Point(lat, lng));
                                } else {
                                    return Mono.error(new RuntimeException("No location found for the given name."));
                                }
                            });
                });
    }


}
