package com.turismea.repository;

import com.turismea.model.Admin;
import com.turismea.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    void removeModeratorById(Long id);
}
