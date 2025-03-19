package com.turismea.repository;

import com.turismea.model.entity.CityDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDistanceRepository extends JpaRepository<CityDistance, Long> {

}
