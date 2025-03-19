package com.turismea.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder, @Value("${google.api.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }

    String URL = "https://routes.googleapis.com/distanceMatrix/";

    @Bean
    public WebClient webClient2(){
        return WebClient.builder().baseUrl(URL).build();
    }

}
