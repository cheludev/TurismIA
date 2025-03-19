package com.turismea.service;

import com.turismea.exception.CityNotFoundException;
import com.turismea.model.entity.City;
import com.turismea.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void deleteCity(Long cityId) {
        if(cityId == null) {
            throw new IllegalArgumentException();
        }
        City city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
        cityRepository.delete(city);
    }

    public Optional<Optional<City>> findByName(String city) {
        return Optional.ofNullable(cityRepository.findByName(city));
    }

    public City save(City city) {
        return cityRepository.save(city);
    }

    public City existOrCreateCity(String city) {
        return cityRepository.findByName(city)
                .orElseGet(() -> cityRepository.save(new City(city)));
    }

}
