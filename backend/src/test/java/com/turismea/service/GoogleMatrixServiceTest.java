package com.turismea.service;

import com.turismea.model.entity.Spot;
import com.turismea.security.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoogleMatrixServiceTest {

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private WebClient webClient;

    @Mock
    private SpotService spotService;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private GoogleMatrixService googleMatrixService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(googleAuthService.getAccessToken()).thenReturn(Mono.just("mocked_token"));

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);

        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("{\"status\": \"OK\"}"));
    }

    @Test
    void testGetDistancesOfVerifiedSpots_Success() {
        Spot spotA = new Spot();
        spotA.setName("Madrid");

        Spot spotB = new Spot();
        spotB.setName("Barcelona");

        List<Spot> spotList = List.of(spotA, spotB);
        List<String> destinations = List.of("Barcelona");

        when(spotService.getDestinationSpots(spotA, spotList)).thenReturn(destinations);

        googleMatrixService.getDistancesOfVerifiedSpots(spotList);

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(any(Function.class));
        verify(requestHeadersSpecMock, times(1)).retrieve();
        verify(responseSpecMock, times(1)).bodyToMono(String.class);
    }
}
