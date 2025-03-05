package com.turismea.service;

import com.turismea.exception.CityNotFoundException;
import com.turismea.model.City;
import com.turismea.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void deleteCity(Long cityId) {
        City city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
        cityRepository.delete(city);
    }
}
