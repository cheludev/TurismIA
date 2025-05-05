package com.turismea.controller;

import ch.qos.logback.core.status.Status;
import com.fasterxml.jackson.annotation.JsonView;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
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
            return ApiResponseUtils.success(
                    "User found successfully", new UserDTO(userOptional.get()));
        } else {
            return ApiResponseUtils.notFound("User with id " + idUser + " not found");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        if (userService.findUserByUsername(user.getUsername()).isPresent()) {
            return ApiResponseUtils.conflict("Username already exists");
        }

        if (user.getRole() != Role.TOURIST) {
            user.setRole(Role.TOURIST);
        }

        User userWithType = userTypeCreator.createUserType(user.getRole(), user);
        User createdUser = userService.signUp(userWithType);

        if (createdUser != null && userService.existUserById(createdUser.getId())) {
            return ApiResponseUtils.created(
                    "User " + createdUser.getUsername() + " created successfully",
                    createdUser.getId());
        } else {
            return ApiResponseUtils.internalServerError("Unexpected error creating user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userService.findUserByUsername(loginRequest.getUsername());

        if (optionalUser.isEmpty()) {
            return ApiResponseUtils.notFound("User not found");
        }

        User user = optionalUser.get();

        if (userService.checkPasswd(user.getPassword(), loginRequest.getPassword())) {
            UserDetails userDetails = repositoryUserDetailsService.loadUserByUsername(user.getUsername());
            Token jwt = jwtTokenProvider.generateToken(userDetails);
            return ApiResponseUtils.success("Login successful", jwt);
        } else {
            return ApiResponseUtils.unauthorized("Incorrect password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponseUtils.unauthorized("No authenticated user");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        switch (user.getRole()) {
            case TOURIST -> response.put("savedRoutes", userService.getTourist(user.getId()).getSavedRoutes());
            case MODERATOR -> response.put("provinceChangesRequest", userService.getModerator(user.getId()).getChangeProvinceRequest());
            case ADMIN -> response.put("requestsToAppliedToChangeTheProvince", userService.getAdmin(user.getId()).getAppliedToChangeTheProvince());
        }

        return ApiResponseUtils.success("Authenticated user data", response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProfile(@RequestBody User user) {
        if (userService.existUserById(user.getId())) {
            User updatedUser = userService.updateUser(user);
            return ApiResponseUtils.success("User updated", updatedUser.toString());
        } else {
            return ApiResponseUtils.notFound("User not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {

        if (!userService.existUserById(id)) {
            return ApiResponseUtils.notFound("User with id " + id + " not found");
        }

        userService.deleteUser(id);
        return ApiResponseUtils.success("User with id " + id + " has been deleted successfully.");
    }



}
