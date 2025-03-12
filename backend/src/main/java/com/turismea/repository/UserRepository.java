package com.turismea.repository;

import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    boolean existsUserById(Long id);

    List<User> getUsersById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName, u.username = :username, " +
            "u.email = :email, u.password = :password, u.role = :role, u.photo = :photo WHERE u.id = :id")
    int updateUser(Long id, String firstName, String lastName, String username,
                   String email, String password, Role role, byte[] photo);
}
