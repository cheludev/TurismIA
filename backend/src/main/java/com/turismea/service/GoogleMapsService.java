package com.turismea.service;

import com.turismea.configuration.GoogleApiProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleMapsService {

    private final WebClient webClient;
    private final GoogleApiProperties googleApiProperties;

    public GoogleMapsService(WebClient webClient, GoogleApiProperties googleApiProperties) {
        this.webClient = webClient;
        this.googleApiProperties = googleApiProperties;
    }

    // Mono<String> is a non-blocking and reactive data type that bring us
    // the option to manage asynchronous data instead of synchronous.

    public Mono<String> getSpots(String ciudad) {

            String API_KEY = googleApiProperties.getKey();

            String requestBody = "{"
                    + "\"textQuery\": \"Museos, monumentos, sitios históricos, atracciones turísticas en " + ciudad + ", España\","
                    + "\"locationBias\": {"
                    + "  \"circle\": {"
                    + "    \"center\": { \"latitude\": 37.2583, \"longitude\": -6.9495 },"
                    + "    \"radius\": 5000"
                    + "  }"
                    + "}"
                    + "}";

            return webClient.post()
                    .uri(uriBuilder ->uriBuilder
                            .path("places:searchText")
                            .queryParam("fields", "name,formatted_address,geometry,place_id")
                            .queryParam("key", API_KEY)
                            .build()
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new RuntimeException("Error 4XX in the Google Places API"))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new RuntimeException("Error 5XX in the Google Places API"))
                    )
                    .bodyToMono(String.class);
    }



}
