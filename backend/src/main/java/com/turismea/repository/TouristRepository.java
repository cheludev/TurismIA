package com.turismea.repository;

import com.turismea.model.Route;
import com.turismea.model.Tourist;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface TouristRepository extends JpaRepository<Tourist, Long> {


}
