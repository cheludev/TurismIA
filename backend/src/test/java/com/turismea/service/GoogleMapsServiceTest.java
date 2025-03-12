package com.turismea.service;

import com.turismea.configuration.GoogleApiProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class GoogleMapsServiceTest {

    private static MockWebServer mockWebServer;
    @InjectMocks
    private GoogleMapsService googleMapsService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetSpots_Success() {
        String fakeResponse = "{ \"places\": [ { \"name\": \"Museo de Huelva\", \"formatted_address\": \"Huelva, España\" } ] }";

        mockWebServer.enqueue(new MockResponse()
                .setBody(fakeResponse)
                .setResponseCode(HttpStatus.OK.value()));

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        GoogleMapsService googleMapsService = new GoogleMapsService(webClient, new GoogleApiProperties());

        Mono<String> responseMono = googleMapsService.getSpots("Huelva");

        StepVerifier.create(responseMono)
                .expectNext(fakeResponse)
                .verifyComplete();
    }

    @Test
    void testGetSpots_Error_4XX() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.BAD_REQUEST.value()));

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        GoogleMapsService googleMapsService = new GoogleMapsService(webClient, new GoogleApiProperties());

        Mono<String> responseMono = googleMapsService.getSpots("Huelva");

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error 4XX in the Google Places API"))
                .verify();
    }

    @Test
    void testGetSpots_Error_5XX() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        GoogleMapsService googleMapsService = new GoogleMapsService(webClient, new GoogleApiProperties());

        Mono<String> responseMono = googleMapsService.getSpots("Huelva");

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error 5XX in the Google Places API"))
                .verify();
    }
}
