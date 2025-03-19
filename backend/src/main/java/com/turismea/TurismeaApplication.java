package com.turismea;

import com.turismea.model.entity.Spot;
import com.turismea.service.CityDistanceService;
import com.turismea.service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        System.out.println("========== Cargando todos los spots desde la BD ==========");

        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            System.out.println("No hay spots en la base de datos.");
        } else {
            System.out.println("Total de spots en la BD: " + spots.size());
            for (Spot spot : spots) {
                System.out.println(" - " + spot.getName() + " (" + spot.getLatitude() + ", " + spot.getLongitude() + ")");
            }

            System.out.println("========== Creando distancias entre spots ==========");
            cityDistanceService.createCityDistances(spots);
            System.out.println("Proceso de cálculo de distancias finalizado.");
        }
    }
}
