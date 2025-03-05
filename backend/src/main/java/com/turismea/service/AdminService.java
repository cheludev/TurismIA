package com.turismea.service;

import com.turismea.model.Admin;
import com.turismea.model.User;
import com.turismea.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin registerAdmin(User user){
        Admin admin = new Admin();
        admin.setUsername(user.getUsername());
        admin.setPassword(user.getPassword());
        admin.setEmail(user.getEmail());;

        adminRepository.save((admin));

        return admin;
    }
}
