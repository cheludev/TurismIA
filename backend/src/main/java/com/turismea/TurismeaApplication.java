package com.turismea;

import com.turismea.model.entity.Spot;
import com.turismea.service.CityDistanceService;
import com.turismea.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
public class TurismeaApplication implements CommandLineRunner {

    @Autowired
    private SpotService spotService;

    @Autowired
    private CityDistanceService cityDistanceService;

    public static void main(String[] args) {
        SpringApplication.run(TurismeaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========== Loading all spots from DB ==========");

        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            System.out.println("No spots found in the database.");
        } else {
            System.out.println("Total spots in the database: " + spots.size());
            for (Spot spot : spots) {
                System.out.println(" - " + spot.getName() + " (" + spot.getLatitude() + ", " + spot.getLongitude() + ")");
            }

            System.out.println("========== Creating distances between spots ==========");

            // Call the sequential distance calculation method
            Mono<Void> distanceCalculationProcess = cityDistanceService.getDistanceMatrixSequential(spots);

            // Block to wait for completion since it's a reactive process in a non-reactive main method
            distanceCalculationProcess.block();

            System.out.println("Distance calculation process completed.");
        }
    }
}
