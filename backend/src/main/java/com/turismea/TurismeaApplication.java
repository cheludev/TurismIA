package com.turismea;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import com.turismea.repository.SpotRepository;
import com.turismea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@SpringBootApplication
public class TurismeaApplication implements CommandLineRunner {

    @Autowired
    private SpotService spotService;

    @Autowired
    private CityDistanceService cityDistanceService;

    @Autowired
    private CityService cityService;

    @Autowired
    private RouteGeneratorService routeGeneratorService;
    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private WKTService wktService;

    public static void main(String[] args) {
        SpringApplication.run(TurismeaApplication.class, args);
    }

    @Override
    public void run(String... args) {

        // 1. Guardamos los spots si no existen
        spotService.saveCitySpots("Huelva").block();
        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            return;
        }

        Optional<City> city = cityService.getCityByName("Huelva");
        if (city.isEmpty()) {
            return;
        }

        // 2. Calculamos distancias entre los spots de esa ciudad
        cityDistanceService.getAllDistances(city.get(), spots);


        // 3. Generar una ruta de prueba
        System.out.println("========== Testing route generation ==========");

        if (spots.size() >= 2) {
            Spot from = spots.get(0);
            Spot to = spots.get(1);

            LocationDTO fromDTO = new LocationDTO(37.259957, -6.947137);
            LocationDTO toDTO = new LocationDTO(37.255766, -6.939415);

            String wktPointA = wktService.createWktPointFromLocation(fromDTO);

            spotService.getNearbySpotsToFromAPoint(wktPointA, 10.0);
            System.out.println("LUGARES MAS CERCANOS AL PUNTO DE INICIO: ");
            spots.forEach(spot -> System.out.println(spot.toString() + "\n"));

            try {
                Route route = routeGeneratorService.generateRoute(fromDTO, toDTO, 734);
                System.out.println("Generated route:");
                route.getSpots().forEach(spot ->
                        System.out.println(" -> " + spot.getName())
                );
                System.out.println("Total duration (s): " + route.getDuration());
            } catch (Exception e) {
                System.err.println("Failed to generate route: " + e.getMessage());
            }
        } else {
            System.out.println("Not enough spots to generate a route.");
        }
    }
}
