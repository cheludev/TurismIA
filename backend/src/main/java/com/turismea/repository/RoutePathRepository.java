package com.turismea.repository;

import com.turismea.model.entity.Route;
import com.turismea.model.entity.RoutePath;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoutePathRepository extends JpaRepository<RoutePath, Long> {

    Optional<RoutePath> findByRoute(Route route);

    void deleteByRoute(Route route);
}
