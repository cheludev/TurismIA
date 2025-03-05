package com.turismea.repository;

import com.turismea.model.Admin;
import com.turismea.model.City;
import com.turismea.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCity(City city);
    List<Location> getLocationByCity(City city);
    List<Location> getLocationByValidatedAndCity(boolean validated, City city);
}
