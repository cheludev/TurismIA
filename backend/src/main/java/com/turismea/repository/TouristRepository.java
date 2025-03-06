package com.turismea.repository;

import com.turismea.model.Route;
import com.turismea.model.Tourist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TouristRepository extends JpaRepository<Tourist, Long> {

    @Query(value = "SELECT t.savedRoutes FROM Tourist t WHERE t.id = :id")
    public List<Route> getSavedRoutes(@Param("id") Long id);

    boolean existsTouristByUsername(String username);

    boolean existsTouristByEmail(String email);
}
