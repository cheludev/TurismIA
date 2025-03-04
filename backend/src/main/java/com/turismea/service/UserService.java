package com.turismea.service;

import com.turismea.model.Moderator;
import com.turismea.model.Role;
import com.turismea.model.Tourist;
import com.turismea.model.User;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TouristRepository touristRepository, ModeratorRepository moderatorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User logIn(User user) {
        if(userRepository.existsUserByUsername(user.getUsername())
                || userRepository.existsUserByEmail(user.getEmail())) {
          return null;
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

    public boolean checkBothPassword(String password1, String password2){
        return password1.equals(password2);
    }

}
