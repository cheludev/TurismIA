package com.turismea.model.dto.ModeratorDTO;

import com.turismea.model.entity.Moderator;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModeratorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private byte[] photo;
    private Province province;

    public ModeratorDTO(Moderator moderator) {
        this.id = moderator.getId();
        this.firstName = moderator.getFirstName();
        this.lastName = moderator.getLastName();
        this.username = moderator.getUsername();
        this.email = moderator.getEmail();
        this.role = moderator.getRole();
        this.photo = moderator.getPhoto();
        this.province = moderator.getProvince();
    }
}