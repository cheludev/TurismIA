package com.turismea.model.dto;

import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import com.turismea.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private byte[] photo;
    private Province province;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.province = user.getProvince();
        this.photo = user.getPhoto();
    }
}
