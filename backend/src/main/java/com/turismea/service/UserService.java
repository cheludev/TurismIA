package com.turismea.service;

import com.turismea.model.Moderator;
import com.turismea.model.Role;
import com.turismea.model.Tourist;
import com.turismea.model.User;
import com.turismea.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tourist registerTourist(User user) {
        Tourist tourist = new Tourist();
        tourist.setUsername(user.getUsername());
        tourist.setPassword(user.getPassword());
        tourist.setEmail(user.getEmail());
        tourist.setRoutes(null);

        return tourist;
    }

    public Moderator registerModerator(User user) {
        Moderator moderator = new Moderator();
        moderator.setUsername(user.getUsername());
        moderator.setPassword(user.getPassword());
        moderator.setEmail(user.getEmail());

        return moderator;
    }

    public Role getRoleFromUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user == null? null : user.getRole();
    }

    public User logIn(User user) {
        if(userRepository.existsUserByUsername(user.getUsername())
                || userRepository.existsUserByEmail(user.getEmail())) {
          return null; //Username change is mandatory due to is already register.
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return user;
    }

    public User signIn(String username, String rawPassWord) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent() && passwordEncoder.matches(rawPassWord, user.get().getPassword())){
            return user.get();
        }

        return null;
    }

    public boolean existUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

    public boolean existEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }


}
