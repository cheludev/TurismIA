package com.turismea.service;

import com.turismea.security.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GoogleSpotServiceTest {

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private GoogleSpotService googleSpotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(any(Function.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(anyString(), anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    }

    @Test
    void testGetSpots_Success() {
        String city = "Huelva";
        String mockedResponse = "{\"status\": \"OK\"}";

        when(googleAuthService.getAccessToken()).thenReturn(Mono.just("mocked_token"));
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(mockedResponse));

        Mono<String> result = googleSpotService.getSpots(city);

        result.subscribe(response -> {
            assert response.equals(mockedResponse);
        });

        verify(webClient, times(1)).post();
        verify(requestBodyUriSpecMock, times(1)).uri(any(Function.class));
        verify(requestBodySpecMock, times(2)).header(anyString(), anyString());
        verify(requestBodySpecMock, times(1)).bodyValue(any());
        verify(requestHeadersSpecMock, times(1)).retrieve();
    }

    @Test
    void testGetSpots_ClientError() {
        String city = "Huelva";

        when(googleAuthService.getAccessToken()).thenReturn(Mono.just("mocked_token"));
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Error 400: Bad Request")));

        Mono<String> result = googleSpotService.getSpots(city);

        result.doOnError(error -> {
            assert error.getMessage().contains("Error 400");
        }).subscribe();

        verify(webClient, times(1)).post();
    }
}
