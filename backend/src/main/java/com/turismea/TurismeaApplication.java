package com.turismea;

import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.service.CityDistanceService;
import com.turismea.service.CityService;
import com.turismea.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class TurismeaApplication implements CommandLineRunner {

    @Autowired
    private SpotService spotService;

    @Autowired
    private CityDistanceService cityDistanceService;
    @Autowired
    private CityService cityService;

    public static void main(String[] args) {
        SpringApplication.run(TurismeaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========== Loading all spots from DB ==========");


        spotService.saveCitySpots("Huelva").block();
        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            System.out.println("No spots found in the database.");
        } else {
            System.out.println("Total spots in the database: " + spots.size());
            for (Spot spot : spots) {
                System.out.println(" - " + spot.getName() + " (" + spot.getLatitude() + ", " + spot.getLongitude() + ")");
            }

            System.out.println("========== Creating distances between spots ==========");

            // Retrieve the City object for "Huelva"
            Optional<City> city = cityService.getCityByName("Huelva");


            if (city.isEmpty()) {
                System.err.println("City 'Huelva' not found in the database.");
                return;
            }

            // Call your distance calculation method
            cityDistanceService.getAllDistances(city.orElse(null), spots);

            System.out.println("Distance calculation process started (async).");
        }
    }

}
