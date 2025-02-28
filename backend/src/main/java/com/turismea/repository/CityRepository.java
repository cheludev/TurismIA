package com.turismea.repository;

import com.turismea.model.City;
import com.turismea.model.CityDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
