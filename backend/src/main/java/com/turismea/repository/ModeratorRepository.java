package com.turismea.repository;

import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    void removeModeratorById(Long id);

    boolean findByUsername(String username);

    boolean existsByUsername(String username);
    @Query("SELECT m FROM Moderator m LEFT JOIN FETCH m.city WHERE m.id = :id")
    Optional<Moderator> findByIdWithCity(@Param("id") Long id);

}
