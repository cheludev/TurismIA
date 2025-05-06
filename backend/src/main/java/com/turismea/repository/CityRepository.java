package com.turismea.repository;

import com.turismea.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);

    boolean existsByName(String name);

    Optional<Object> getCityById(Long id);

    @Query("SELECT c FROM City c LEFT JOIN FETCH c.spots WHERE c.name = :name")
    Optional<City> findByNameWithSpots(@Param("name") String name);

    @Query("SELECT c FROM City c LEFT JOIN FETCH c.spots WHERE c.id = :id")
    Optional<City> findByIdWithSpots(@Param("id") Long id);


}
