package com.turismea.service;

import com.turismea.model.entity.Admin;
import com.turismea.model.entity.User;
import com.turismea.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterAdmin() {
        User user = new User();
        user.setUsername("adminUser");
        user.setPassword("securePass");
        user.setEmail("admin@example.com");

        Admin adminMock = new Admin();
        adminMock.setUsername(user.getUsername());
        adminMock.setPassword("encodedPassword");
        adminMock.setEmail(user.getEmail());

        when(passwordEncoder.encode("securePass")).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        Admin createdAdmin = adminService.registerAdmin(user);

        assertNotNull(createdAdmin);
        assertEquals("adminUser", createdAdmin.getUsername());
        assertEquals("encodedPassword", createdAdmin.getPassword());
        assertEquals("admin@example.com", createdAdmin.getEmail());

        verify(passwordEncoder).encode("securePass");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testFindFirstByOrderByIdAsc() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("firstAdmin");

        when(adminRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.findFirstByOrderByIdAsc();

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("firstAdmin", result.get().getUsername());
    }

    @Test
    void testFindFirstByOrderByIdAsc_NotFound() {
        when(adminRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        Optional<Admin> result = adminService.findFirstByOrderByIdAsc();

        assertFalse(result.isPresent());
    }

    @Test
    void testSave() {
        Admin admin = new Admin();
        admin.setUsername("adminToSave");

        when(adminRepository.save(admin)).thenReturn(admin);

        Admin savedAdmin = adminService.save(admin);

        assertNotNull(savedAdmin);
        assertEquals("adminToSave", savedAdmin.getUsername());
        verify(adminRepository).save(admin);
    }
}
