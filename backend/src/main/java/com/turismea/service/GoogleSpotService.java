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

    public Mono<String> getSpots(String ciudad) {
        String ACCESS_TOKEN = googleAuthService.obtenerAccessToken();

        String requestBody = "{"
                + "\"textQuery\": \"" + ciudad + "\""
                + "}";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/places:searchText")
                        .queryParam("fields","*")
                        .build()
                )
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                        return Mono.error(new RuntimeException("Error 401: Invalid access token"));
                    }
                    return Mono.error(new RuntimeException("Error 4XX: Client error in the Google Places API"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Error 5XX: Server error in the Google Places API"))
                )
                .bodyToMono(String.class);
    }
}

