package com.turismea.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.turismea.model.entity.User;
import com.turismea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @JsonView(User.class)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long idUser) {
        Optional<User> userOptional = userService.findUserById(idUser);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
