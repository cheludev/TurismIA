package com.turismea.service;

import com.turismea.model.entity.User;
import com.turismea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp_UserAlreadyExists_ReturnsNull() {
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("existing@email.com");
        user.setPassword("password123");

        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);

        User result = userService.signUp(user);
        assertNull((result));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignUp_NewUser_SavesAndReturnsUser() {
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("new@email.com");
        user.setPassword("password123");

        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        User result = userService.signUp(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogIn_CorrectCredentials_ReturnsUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        User result = userService.logIn("testUser", "rawPassword");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testLogIn_WrongPassword_ReturnsNull() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        User result = userService.logIn("testUser", "wrongPassword");

        assertNull(result);
    }

    @Test
    void testExistByUsername_UserExists_ReturnsTrue() {
        when(userRepository.existsUserByUsername("existingUser")).thenReturn(true);

        boolean result = userService.existByUsername("existingUser");

        assertTrue(result);
    }

    @Test
    void testExistUsername_UserDoesNotExist_By_ReturnsFalse() {
        when(userRepository.existsUserByUsername("nonExistentUser")).thenReturn(false);

        boolean result = userService.existByUsername("nonExistentUser");

        assertFalse(result);
    }

    @Test
    void testExistEmail_EmailExists_ReturnsTrue() {
        when(userRepository.existsUserByEmail("existing@email.com")).thenReturn(true);

        boolean result = userService.existEmail("existing@email.com");

        assertTrue(result);
    }

    @Test
    void testExistEmail_EmailDoesNotExist_ReturnsFalse() {
        when(userRepository.existsUserByEmail("nonexistent@email.com")).thenReturn(false);

        boolean result = userService.existEmail("nonexistent@email.com");

        assertFalse(result);
    }

    @Test
    void testCheckBothPassword_PasswordsMatch_ReturnsTrue() {
        boolean result = userService.checkBothPassword("password123", "password123");
        assertTrue(result);
    }

    @Test
    void testCheckBothPassword_PasswordsDoNotMatch_ReturnsFalse() {
        boolean result = userService.checkBothPassword("password123", "password456");
        assertFalse(result);
    }
}
