package com.turismea.controller;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.AdminDTO;
import com.turismea.model.dto.RequestDTO.ManageRequestDTO;
import com.turismea.model.entity.Admin;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.service.AdminService;
import com.turismea.service.RequestService;
import com.turismea.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final RequestService requestService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService, UserService userService, RequestService requestService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.userService = userService;
        this.requestService = requestService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/requests/{id}/province")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> manageProvinceChange(@PathVariable Long id, @RequestParam Province province) {
        requestService.manageRequest(id, RequestStatus.APPROVED, province);
        return ApiResponseUtils.success("Cambio de provincia gestionado");
    }


    // 1️⃣ GET - Obtener datos del admin
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDTO>> getAdminInfo(@PathVariable Long id) {

        User authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't access another admin's data", null);
        }

        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return ApiResponseUtils.success("Admin data retrieved successfully", new AdminDTO(admin));
    }

    // 2️⃣ PUT - Actualizar datos personales del admin
    @PutMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDTO>> editAdmin(@PathVariable Long id, @RequestBody AdminDTO userDTO) {

        User authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't edit another admin's data", null);
        }

        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        admin.setFirstName(userDTO.getFirstName());
        admin.setLastName(userDTO.getLastName());
        admin.setEmail(userDTO.getEmail());
        admin.setProvince(userDTO.getProvince());

        Admin savedAdmin = adminService.save(admin);

        return ApiResponseUtils.success("Admin data updated successfully", new AdminDTO(savedAdmin));
    }

    // 3️⃣ DELETE - Eliminar mi cuenta de admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Long id) {

        User authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't delete another admin's account", null);
        }

        userService.deleteUser(id);

        return ApiResponseUtils.success("Admin account deleted successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminDTO>> createAdmin(@RequestBody AdminDTO adminDTO) {



        // Crear objeto User a partir del DTO
        User user = new User();
        user.setUsername(adminDTO.getUsername());
        user.setEmail(adminDTO.getEmail());
        user.setPassword(passwordEncoder.encode(adminDTO.getPasswd()));
        user.setFirstName(adminDTO.getFirstName());
        user.setLastName(adminDTO.getLastName());
        user.setProvince(adminDTO.getProvince());
        user.setRole(com.turismea.model.enumerations.Role.ADMIN);

        if (userService.existByUsername(user.getUsername())) {
            return ApiResponseUtils.conflict("Username already exists", null);
        }
        if (userService.existEmail(user.getEmail())) {
            return ApiResponseUtils.conflict("Email already exists", null);
        }
        Admin admin = adminService.registerAdmin(user);
        admin.setPassword("do you wanna see me??? jajajaja");
        return ApiResponseUtils.created("Admin created successfully", new AdminDTO(admin));
    }

    @PutMapping("/requests/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> manageRequest(@RequestBody ManageRequestDTO dto) {

        requestService.manageRequest(dto.getId(), dto.getStatus(), dto.getProvince());

        return ApiResponseUtils.success("Request managed successfully");
    }

}
