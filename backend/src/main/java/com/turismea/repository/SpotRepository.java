package com.turismea.repository;

import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT s.*, ST_Distance_Sphere(ST_GeomFromText(:point, 4326), ) AS distance" +
            "FROM Spot s" +
            "WHERE ST_Distance_Sphere(coordinates, ST_GeomFromText(:point, 4326)) <= :radius AS distance" +
            "ORDER BY distance"
            , nativeQuery = true)
    List<Spot> getNearbySpotsToFromAPoint(@Param(value = "point") String wktPoint, double radius);

    @Query(value = "SELECT ST_Distance_Sphere(ST_GeomFromText(:pointA, 4326), ST_GeomFromText(:pointB, 4326))",
            nativeQuery = true)
    Double getDistanceBetween(
            @Param("pointA") String wktPointA,
            @Param("pointB") String wktPointB
    );

}
