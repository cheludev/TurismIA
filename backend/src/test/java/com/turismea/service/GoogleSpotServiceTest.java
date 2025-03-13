package com.turismea.service;

import com.turismea.security.GoogleAuthService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class GoogleSpotServiceTest {

    private static MockWebServer mockWebServer;
    private GoogleAuthService googleAuthService;
    private GoogleSpotService googleSpotService;

    @BeforeAll
    static void setUpMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownMockServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        googleAuthService = Mockito.mock(GoogleAuthService.class);
        Mockito.when(googleAuthService.obtenerAccessToken()).thenReturn("fake-access-token");


        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        googleSpotService = new GoogleSpotService(webClient, googleAuthService);
    }

    @Test
    void testGetSpots_Success() {
        String fakeResponse = "{ \"places\": [ { \"displayName\": { \"text\": \"Museo de Huelva\" }, "
                + "\"formattedAddress\": \"Huelva, España\", "
                + "\"location\": { \"latitude\": 37.2619, \"longitude\": -6.9427 }, "
                + "\"placeId\": \"ChIJxQvW8WfYQw0Rc2P8sdzG0mA\" } ] }";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(fakeResponse));

        Mono<String> result = googleSpotService.getSpots("Huelva");

        StepVerifier.create(result)
                .expectNext(fakeResponse)
                .verifyComplete();
    }

    @Test
    void testGetSpots_4xxError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.BAD_REQUEST.value()));

        Mono<String> result = googleSpotService.getSpots("Huelva");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                                && throwable.getMessage().equals("Error 4XX: Client error in the Google Places API"))
                .verify();
    }

    @Test
    void testGetSpots_5xxError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Mono<String> result = googleSpotService.getSpots("Huelva");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                                && throwable.getMessage().equals("Error 5XX: Server error in the Google Places API"))
                .verify();
    }
}
