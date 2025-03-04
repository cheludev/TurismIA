package com.turismea.service;

import com.turismea.exception.LocationNotFoundException;
import com.turismea.model.Location;
import com.turismea.model.Moderator;
import com.turismea.repository.LocationRepository;
import jakarta.persistence.Lob;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void validateLocation(Moderator moderator, Location location){
        location.setValidated(true);
    }

    public Location newTouristicLocation(Long locationId, String touristicInfo) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        if(!location.isValidated()) {
            location.setValidated(true);
        }

        location.setInfo(touristicInfo);
        return location;

    }

    public List<Location> getValidatedLocationByCity(String city) {
        return locationRepository.getLocationByValidatedAndCity(true, city);
    }

    public List<Location> getAllLocationByCity(String city) {
        return locationRepository.getLocationByCity(city);
    }

}
