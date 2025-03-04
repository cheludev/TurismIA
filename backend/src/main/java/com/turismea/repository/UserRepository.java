package com.turismea.repository;

import com.turismea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    boolean existsUserById(Long id);
}
