package com.turismea.repository;

import com.turismea.model.entity.City;
import com.turismea.model.entity.CityDistance;
import com.turismea.model.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityDistanceRepository extends JpaRepository<CityDistance, Long> {

    @Query(value = "SELECT c.spotB FROM CityDistance c WHERE c.spotA = :idInitSpot")
    List<Spot> getConnections(@Param("idInitSpot") Spot idSpot);

    @Query("SELECT cd FROM CityDistance cd " +
            "WHERE (cd.spotA = :spot1 AND cd.spotB = :spot2) " +
            "OR (cd.spotA = :spot2 AND cd.spotB = :spot1)")
    List<CityDistance> findBySpotsIgnoreOrder(@Param("spot1") Spot spot1, @Param("spot2") Spot spot2);

    @Query("SELECT cd FROM CityDistance cd WHERE cd.spotA = :spot OR cd.spotB = :spot")
    List<CityDistance> findAllConnectionsOf(@Param("spot") Spot spot);

    boolean existsByCityAndSpotAAndSpotB(City city, Spot spotA, Spot spotB);

    @Query("SELECT COUNT(cd) > 0 FROM CityDistance cd WHERE cd.city = :city AND ((cd.spotA = :spotA AND cd.spotB = :spotB) OR (cd.spotA = :spotB AND cd.spotB = :spotA))")
    boolean existsByCityAndSpotsIgnoreOrder(@Param("city") City city, @Param("spotA") Spot spotA, @Param("spotB") Spot spotB);


}
