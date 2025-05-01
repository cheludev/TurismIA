package com.turismea.repository;

import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    void removeModeratorById(Long id);

    boolean findByUsername(String username);

    boolean existsByUsername(String username);

}
