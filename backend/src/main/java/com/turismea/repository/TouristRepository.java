package com.turismea.repository;

import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface TouristRepository extends JpaRepository<Tourist, Long> {

    boolean existsTouristByUsername(String username);

    boolean existsTouristByEmail(String email);

    Optional<Tourist> findByUsername(String username);

    @Query("SELECT t FROM Tourist t LEFT JOIN FETCH t.savedRoutes WHERE t.id = :id")
    Optional<Tourist> findByIdWithSavedRoutes(@Param("id") Long id);



}
