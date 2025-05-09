package com.turismea.service;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.entity.*;
import com.turismea.repository.AdminRepository;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mysql.cj.conf.PropertyKey.logger;
import static org.hibernate.internal.CoreLogging.logger;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TouristRepository touristRepository;
    private final ModeratorRepository moderatorRepository;
    private final AdminRepository adminRepository;
    private final UserTypeCreator userTypeCreator;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminRepository adminRepository,
                       TouristRepository touristRepository, ModeratorRepository moderatorRepository, UserTypeCreator userTypeCreator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.touristRepository = touristRepository;
        this.moderatorRepository = moderatorRepository;


        this.adminRepository = adminRepository;
        this.userTypeCreator = userTypeCreator;
    }

    public User signUp(User user) {

        if(user.getUsername() == null || user.getPassword() == null
                || user.getUsername().isEmpty() || user.getPassword().isEmpty()){
            return null;
        }

        if(userRepository.existsUserByUsername(user.getUsername())
                || userRepository.existsUserByEmail(user.getEmail())) {
            return null;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User logIn(String username, String rawPassWord) {
        if(username.isEmpty() || rawPassWord.isEmpty()){
            return null;
        }
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent() && passwordEncoder.matches(rawPassWord, user.get().getPassword())){
            return user.get();
        }

        return null;
    }

    public boolean existByUsername(String username) {
        if(username.isEmpty()){
            return false;
        }
        return userRepository.existsUserByUsername(username);
    }

    public boolean existEmail(String email) {
        if(email == null || email.isEmpty()){
            return false;
        }
        return userRepository.existsUserByEmail(email);
    }

    public boolean checkBothPassword(String password1, String password2){
        if(password1 == null || password2 == null || password2.isEmpty() || password1.isEmpty()){
            return false;
        }
        return password1.equals(password2);
    }


    @Transactional
    public User updateUser(User user) {
        if (user.getUsername() == null) {
            throw new UserNotFoundException(user.getId());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }


    public boolean existUserById(long id){
        return userRepository.existsUserById(id);
    }

    public Optional<User> findUserById(long id){
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean checkPasswd(String encodedPasswd, String rawPassword) {
        return passwordEncoder.matches(rawPassword, encodedPasswd);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public boolean existsUserByUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

    public Tourist getTourist(Long userId) {
        return touristRepository.findByIdWithSavedRoutes(userId)
                .orElseThrow(() -> new UserNotFoundException("Tourist not found"));
    }



    public Moderator getModerator(Long userId) {
        return moderatorRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Moderator not found"));
    }

    public Admin getAdmin(Long userId) {
        return adminRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Admin not found"));
    }

    public User getUserFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username;
        if (auth.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = auth.getName();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }



    @Transactional
    public void deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            switch (user.getRole()) {
                case TOURIST -> {
                    touristRepository.deleteById(id);
                }
                case MODERATOR -> {
                    moderatorRepository.deleteById(id);
                }
                case ADMIN -> {
                    adminRepository.deleteById(id);
                }
            }

            userRepository.deleteById(id);

        } else {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

}
