package com.turismea.service;

import com.turismea.exception.AlreadyAppliedException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.entity.Request;
import com.turismea.model.entity.Tourist;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.RequestRepository;
import com.turismea.repository.TouristRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TouristServiceTest {

    @Mock
    private TouristRepository touristRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TouristService touristService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_userAlreadyExist() {
        Tourist tourist = new Tourist();
        tourist.setUsername("existingUser");
        tourist.setEmail("existing@email.com");
        tourist.setPassword("password123");

        when(touristRepository.existsTouristByUsername(tourist.getUsername())).thenReturn(true);

        Tourist result = touristService.registerTourist(tourist);

        assertNull(result);
        verify(touristRepository, never()).save(any(Tourist.class));
    }

    @Test
    void testRegister_userNotExist() {
        Tourist tourist = new Tourist();
        tourist.setUsername("newUser");
        tourist.setEmail("new@email.com");
        tourist.setPassword("password123");

        when(touristRepository.existsTouristByUsername(anyString())).thenReturn(false);
        when(touristRepository.existsTouristByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(touristRepository.save(any(Tourist.class))).thenAnswer(invocation -> {
            Tourist t = invocation.getArgument(0);
            t.setRole(Role.TOURIST);
            return t;
        });

        Tourist result = touristService.registerTourist(tourist);

        assertNotNull(result, "The user should not be null");
        assertEquals("encodedPassword", result.getPassword(), "The password should be encoded");
        assertEquals(Role.TOURIST, result.getRole(), "The role should be TOURIST");
        verify(touristRepository).save(any(Tourist.class));
    }



    @Test
    void testApplyToModerator_UserExist_ItIsAlreadyAModerator() {
        Long id = 1L;
        Province province = Province.HUELVA;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setUsername("newUser");
        fakeTourist.setEmail("new@email.com");
        fakeTourist.setPassword("password123");
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.MODERATOR);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));

        boolean result =  touristService.applyToModerator(id, province, "reasons");

        assertFalse(result);
        verify(touristRepository).findById(id);
    }

    @Test
    void testApplyToModerator_UserExist_ItIsNotAModerator() {
        Long id = 1L;
        Province province = Province.HUELVA;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));
        when(requestRepository.existsByUserAndType(fakeTourist, RequestType.TO_MODERATOR)).thenReturn(false);

        boolean result = touristService.applyToModerator(id, province, "reasons");

        assertTrue(result);
        verify(requestService).save(any(Request.class));
    }


    @Test
    void testApplyToModerator_UserAlreadyApplied() {
        Long id = 1L;
        Province province = Province.HUELVA;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));
        when(requestService.existsByUserAndType(fakeTourist, RequestType.TO_MODERATOR)).thenReturn(true);

        assertThrows(AlreadyAppliedException.class, () -> touristService.applyToModerator(id, province, "reasons"));

        verify(requestService, never()).save(any(Request.class));
    }


    @Test
    void testApplyToModerator_UserNotExist() {
        Long id = 1L;
        Province province = Province.HUELVA;

        when(touristRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> touristService.applyToModerator(id, province, "reasons"));

        verify(touristRepository).findById(id);
    }

    @Test
    void testEditTourist_UserNotExist() {
        Long id = 1L;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setUsername("newUser");
        fakeTourist.setEmail("new@email.com");
        fakeTourist.setPassword("password123");
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> touristService.editTourist(id, fakeTourist));

        verify(touristRepository).findById(id);
    }

    @Test
    void testEditTourist_UserExist() {
        Long id = 1L;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setUsername("newUser");
        fakeTourist.setEmail("new@email.com");
        fakeTourist.setPassword("password123");
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));
        when(touristRepository.save(any(Tourist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tourist infoTourist = new Tourist();
        infoTourist.setUsername("newUserEDITED");
        infoTourist.setEmail("new@email.com");
        infoTourist.setPassword("password123");
        infoTourist.setId(id);
        infoTourist.setRole(Role.TOURIST);

        Tourist editedTourist = touristService.editTourist(id, infoTourist);
        assertEquals("newUserEDITED", editedTourist.getUsername());

        verify(touristRepository).findById(id);
        verify(touristRepository).save(fakeTourist);
    }


}
