package com.turismea.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean(name = "placesWebClient")
    public WebClient placesWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://places.googleapis.com").build();
    }

    @Bean(name = "osrmWebClient")
    public WebClient osrmWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:5000/route/v1").build();
    }
}

