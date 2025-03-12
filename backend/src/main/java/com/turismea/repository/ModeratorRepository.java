package com.turismea.repository;

import com.turismea.model.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    void removeModeratorById(Long id);
}
