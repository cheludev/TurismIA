package com.turismea.repository;

import com.turismea.model.Route;
import com.turismea.model.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findRouteByOwner_Id(Long ownerId);
    List<Route> getRoutesByCity(String city);
    List<Route> getRoutesByOwner(Tourist owner);
}
