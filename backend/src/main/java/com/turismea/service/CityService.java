package com.turismea.service;

import com.turismea.exception.CityNotFoundException;
import com.turismea.model.entity.City;
import com.turismea.repository.CityRepository;
import com.turismea.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository, RouteRepository routeRepository) {
        this.cityRepository = cityRepository;
    }

    public void deleteCity(Long cityId) {
        if(cityId == null) {
            throw new IllegalArgumentException();
        }
        City city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
        cityRepository.delete(city);
    }

    public Optional<City> findByName(String city) {
        return cityRepository.findByName(city);
    }

    public City save(City city) {
        return cityRepository.save(city);
    }

    public City existOrCreateCity(String city) {
        return cityRepository.findByName(city)
                .orElseGet(() -> cityRepository.save(new City(city)));
    }

    public Optional<City> getCityByName(String city) {
        return cityRepository.findByName(city);
    }

    public Optional<City> getCityById(Long cityId) {
        return cityRepository.findById(cityId);
    }

    public Optional<City> findById(Long cityId) {
        return cityRepository.findById(cityId);
    }
}
