package com.turismea.controller;

import ch.qos.logback.core.status.Status;
import com.fasterxml.jackson.annotation.JsonView;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.dto.LoginRequest;
import com.turismea.model.dto.UserDTO;
import com.turismea.model.entity.Admin;
import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Role;
import com.turismea.security.CustomUserDetails;
import com.turismea.security.RepositoryUserDetailsService;
import com.turismea.security.jwt.JwtTokenProvider;
import com.turismea.security.jwt.Token;
import com.turismea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TouristService touristService;
    @Autowired
    private UserTypeCreator userTypeCreator;
    @Autowired
    private ModeratorService moderatorService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RepositoryUserDetailsService repositoryUserDetailsService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") long idUser) {
        Optional<User> userOptional = userService.findUserById(idUser);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok().body(new UserDTO(user));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "not_found",
                            "message", "User with id " + idUser + " not found"
                    ));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        Optional<User> userOptional = userService.findUserByUsername(user.getUsername());

        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", "conflict",
                            "message", "Username already exists"
                    ));
        } else {
            if (user.getRole() != Role.TOURIST) {
                user.setRole(Role.TOURIST);
            }

            User userWithType = userTypeCreator.createUserType(user.getRole(), user);

            User createdUser = userService.signUp(userWithType);


            if (createdUser != null && userService.existUserById(createdUser.getId())) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "status", "created",
                                "message", "User " + createdUser.getUsername() + " with id(" + createdUser.getId() + ") has been created correctly.",
                                "userId", createdUser.getId()
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "status", "error",
                                "message", "Unexpected error creating user"
                        ));
            }
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userService.findUserByUsername(loginRequest.getUsername());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));
        }

        User user = optionalUser.get();

        if (userService.checkPasswd(user.getPassword(), loginRequest.getPassword())) {

            // Cargamos el UserDetails desde tu RepositoryUserDetailsService
            UserDetails userDetails = repositoryUserDetailsService.loadUserByUsername(user.getUsername());

            Token jwt = jwtTokenProvider.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "token", jwt
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Incorrect password"
            ));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "No authenticated user"
            ));
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        switch (user.getRole()) {
            case TOURIST -> {
                Tourist tourist = userService.getTourist(user.getId());
                response.put("savedRoutes", tourist.getSavedRoutes());
            }
            case MODERATOR -> {
                Moderator moderator = userService.getModerator(user.getId());
                response.put("provinceChangesRequest", moderator.getChangeProvinceRequest());
            }
            case ADMIN -> {
                Admin admin = userService.getAdmin(user.getId());
                response.put("requestsToAppliedToChangeTheProvince", admin.getAppliedToChangeTheProvince());
            }
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProfile(@RequestBody User user){
        if(userService.existUserById(user.getId())){
            User userAux = userService.updateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "User updated",
                            "body", userAux.toString()
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "Not found user"
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {

        if (!userService.existUserById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User with id " + id + " not found"
            ));
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User with id " + id + " has been deleted successfully."
        ));
    }




}
