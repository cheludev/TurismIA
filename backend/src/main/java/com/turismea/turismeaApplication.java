package com.turismea;

import com.turismea.service.GoogleSpotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class turismeaApplication {
    public static void main(String[] args) {
        SpringApplication.run(turismeaApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ApplicationContext context) {
        return args -> {
            GoogleSpotService googleSpotService = context.getBean(GoogleSpotService.class);

            googleSpotService.getSpots("Huelva")
                    .doOnNext(response -> {
                        System.out.println("Lugares en Huelva:");
                        System.out.println(response);
                    })
                    .block();
        };
    }
}
