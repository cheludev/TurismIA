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

    public static void main(String[] args) {
        SpringApplication.run(TurismeaApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
