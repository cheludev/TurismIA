package com.turismea.service;

import com.turismea.model.entity.Admin;
import com.turismea.model.entity.User;
import com.turismea.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(User user){
        if (user == null) {
            throw new IllegalArgumentException("User can not be null");
        }

        Admin admin = new Admin();
        admin.setUsername(user.getUsername());
        admin.setPassword(passwordEncoder.encode(user.getPassword()));
        admin.setEmail(user.getEmail());

        return adminRepository.save(admin);
    }

    public Optional<Admin> findFirstByOrderByIdAsc() {
        return adminRepository.findFirstByOrderByIdAsc();
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}
