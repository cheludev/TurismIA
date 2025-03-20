package com.turismea.service;

import com.turismea.model.dto.routesDTO.GoogleRouteResponse;
import com.turismea.model.entity.City;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityDistanceRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CityDistanceService {

    private final CityDistanceRepository cityDistanceRepository;
    private final GoogleRoutesService googleRoutesService;

    @Autowired
    public CityDistanceService(CityDistanceRepository cityDistanceRepository, GoogleRoutesService googleRoutesService) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.googleRoutesService = googleRoutesService;
    }

    public void saveCityDistances(List<CityDistance> spotDistancesList) {
        cityDistanceRepository.saveAll(spotDistancesList);
    }


    public CityDistance save(CityDistance cityDistance) {
        return cityDistanceRepository.save(cityDistance);

    }

    public Mono<Void> getDistanceMatrixSequential(List<Spot> spots) {
        return googleRoutesService.getDistanceMatrixSequential(spots);
    }
}
