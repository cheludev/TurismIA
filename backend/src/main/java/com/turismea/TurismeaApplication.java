package com.turismea;

import com.turismea.service.SpotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TurismeaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TurismeaApplication.class, args);

        SpotService spotService = context.getBean(SpotService.class);

        spotService.saveCitySpots("Huelva");
    }
}
