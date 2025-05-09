package com.turismea.model.dto;

import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String passwd;
    private String email;
    private Role role;
    private byte[] photo;
    private Province province;

    public AdminDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.passwd = user.getPassword();
        this.role = user.getRole();
        this.province = user.getProvince();
        this.photo = user.getPhoto();
    }
}
