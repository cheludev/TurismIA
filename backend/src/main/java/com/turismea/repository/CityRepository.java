package com.turismea.repository;

import com.turismea.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);

    boolean existsByName(String name);

    Optional<Object> getCityById(Long id);
}
