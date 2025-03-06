package com.turismea.repository;

import com.turismea.model.City;
import com.turismea.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByCity(City city);
    List<Spot> getSpotByCity(City city);
    List<Spot> getSpotByValidatedAndCity(boolean validated, City city);
}
