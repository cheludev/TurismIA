package com.turismea.service;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.User;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    public User signUp(User user) {

        if(user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getUsername() == null || user.getPassword() == null){
            return null;
        }

        if(userRepository.existsUserByUsername(user.getUsername())
                || userRepository.existsUserByEmail(user.getEmail())) {
          return null;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return user;
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

    public boolean existUsername(String username) {
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

//    @Transactional
//    public void updateUser(Long id, String firstName, String lastName, String username,
//                           String email, String password, Role role, byte[] photo) {
//        int rowsAffected = userRepository.updateUser(id, firstName, lastName, username, email, password, role, photo);
//        if (rowsAffected == 0) {
//            throw new UserNotFoundException(id);
//        }
//    }

    @Transactional
    public void updateUser(User user) {
        if(user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getUsername() == null || user.getPassword() == null){
            throw new UserNotFoundException(user.getId());
        }
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }

        userRepository.save(user);
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
}
