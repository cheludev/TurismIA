package com.turismea.repository;

import com.turismea.model.entity.City;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @EntityGraph(attributePaths = "spots")
    Optional<Route> findWithSpotsById(Long id);


    @EntityGraph(attributePaths = "spots")
    List<Route> findAll();

    List<Route> findRouteByOwner_Id(Long ownerId);

    List<Route> getRoutesByCity(City city);
    @Query("SELECT DISTINCT r FROM Route r LEFT JOIN FETCH r.spots WHERE r.owner = :owner AND r.draft = true")
    List<Route> getSavedDraftRoutesByOwnerFetchSpots(@Param("owner") Tourist owner);


    List<Route> getRoutesByOwner(Tourist tourist);

    @EntityGraph(attributePaths = "spots")
    List<Route> findByOwner_IdAndDraft(Long id, boolean draft);

    Optional<Route> findByIdAndOwner_Id(Long id, Long id1);

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.spots WHERE r.owner.id = :userId AND r.draft = true")
    List<Route> findSavedRoutesByUserId(@Param("userId") Long userId);

    List<Route> findByCityId(Long cityId);

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.spots WHERE r.city.name = :cityName")
    List<Route> findByCity_NameIgnoreCase(@Param("cityName") String cityName);
}

