package com.turismea.service;


import com.turismea.exception.MissingProvinceException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.*;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.ModeratorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ModeratorServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModeratorRepository moderatorRepository;

    @InjectMocks
    private ModeratorService moderatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterModerator_userExist(){
        User user = new User();
        user.setUsername("Juan_365");
        user.setPassword("passwd");
        user.setId(1L);
        Province province = Province.HUELVA;

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));

        Moderator answer = moderatorService.registerModerator(user, province);
        assertNotNull(answer, "The user should not be empty");
        assertEquals(Role.MODERATOR, answer.getRole());
        assertEquals("encodedPassword", answer.getPassword());

        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void testRegisterModerator_userDoesNotExist(){
        User user = new User();
        user.setUsername("Juan_365");
        user.setId(1L);
        Province province = Province.HUELVA;

        when(userService.findUserById(user.getId())).thenThrow(new UserNotFoundException(1L));
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(user.getId()));

        verify(userService).findUserById(1L);
        verify(moderatorRepository, never()).save(any(Moderator.class));
    }

    @Test
    void testRegisterModerator_missingProvince() {
        User user = new User();
        user.setUsername("Juan_365");
        user.setId(1L);

        when(userService.findUserById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(MissingProvinceException.class, () -> moderatorService.registerModerator(user, null));
        assertThrows(MissingProvinceException.class, () -> moderatorService.registerModerator(user, Province.NO_PROVINCE));

        verify(moderatorRepository, never()).save(any(Moderator.class));
    }



}
