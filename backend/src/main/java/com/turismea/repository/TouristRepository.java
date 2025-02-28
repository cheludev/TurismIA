package com.turismea.repository;

import com.turismea.model.Route;
import com.turismea.model.Tourist;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Repository

public interface TouristRepository extends JpaRepository<Tourist, Long> {

    @Query(value = "SELECT t.savedRoutes FROM Tourist t WHERE T.id = :id")
    public List<Route> getSavedRoutes(@Param("id") Long id);
}
