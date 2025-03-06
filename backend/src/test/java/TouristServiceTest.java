import com.turismea.exception.UserNotFoundException;
import com.turismea.model.Request;
import com.turismea.model.Tourist;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.RequestRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.service.TouristService;
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
    private ModeratorRepository moderatorRepository;

    @Mock
    private RequestRepository requestRepository;

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

        when(touristRepository.existsTouristByUsername(tourist.getUsername())).thenReturn(false);
        when(touristRepository.existsTouristByEmail(tourist.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(tourist.getPassword())).thenReturn("encodedPassword");

        Tourist savedTourist = new Tourist();
        savedTourist.setUsername(tourist.getUsername());
        savedTourist.setEmail(tourist.getEmail());
        savedTourist.setPassword("encodedPassword");

        when(touristRepository.save(any(Tourist.class))).thenReturn(savedTourist);

        Tourist result = touristService.registerTourist(tourist);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.TOURIST, result.getRole());
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
        fakeTourist.setUsername("newUser");
        fakeTourist.setEmail("new@email.com");
        fakeTourist.setPassword("password123");
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));
        when(requestRepository.existsByUserAndType(fakeTourist, RequestType.TO_MODERATOR)).thenReturn(false);

        boolean result = touristService.applyToModerator(id, province, "reasons");

        assertTrue(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void testApplyToModerator_UserAlreadyApplied() {
        Long id = 1L;
        Province province = Province.HUELVA;

        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(id);
        fakeTourist.setRole(Role.TOURIST);

        when(touristRepository.findById(id)).thenReturn(Optional.of(fakeTourist));
        when(requestRepository.existsByUserAndType(fakeTourist, RequestType.TO_MODERATOR)).thenReturn(true);

        boolean result = touristService.applyToModerator(id, province, "reasons");

        assertFalse(result); // ✅ Debe devolver false porque ya aplicó
        verify(requestRepository, never()).save(any(Request.class)); // ✅ No debería guardar una nueva solicitud
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
