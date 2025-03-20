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

    @Bean(name = "routesWebClient")
    public WebClient routesWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://routes.googleapis.com")
                .filter(loggingFilter())
                .build();
    }

    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("➡️ Sending request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> logger.info("➡️ {}: {}", name, value)));
            return Mono.just(clientRequest);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("⬅️ Received response: {} {}", clientResponse.statusCode(), clientResponse.headers().asHttpHeaders());
            return clientResponse.bodyToMono(String.class)
                    .doOnNext(body -> logger.info("⬅️ Response Body: {}", body))
                    .then(Mono.just(clientResponse));
        }));
    }

    @Bean(name = "placesWebClient")
    public WebClient placesWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://places.googleapis.com").build();
    }
}

