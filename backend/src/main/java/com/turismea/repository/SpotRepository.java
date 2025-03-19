package com.turismea.repository;

import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByCity(City city);
    List<Spot> getSpotByCity(City city);
    List<Spot> getSpotByValidatedAndCity(boolean validated, City city);

    Spot findByName(String name);

    List<Spot> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query("SELECT s.name FROM Spot s")
    List<String> findAllNames();
}
