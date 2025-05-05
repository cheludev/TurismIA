package com.turismea.model.dto.TouristDTO;

import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class TouristResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private byte[] photo;
    private Province province;
    private Role role;

    private List<Long> savedRouteIds;

    public TouristResponseDTO(Tourist tourist) {
        this.id = tourist.getId();
        this.username = tourist.getUsername();
        this.email = tourist.getEmail();
        this.firstName = tourist.getFirstName();
        this.lastName = tourist.getLastName();
        this.photo = tourist.getPhoto();
        this.province = tourist.getProvince();
        this.role = tourist.getRole();

        if (tourist.getSavedRoutes() != null) {
            this.savedRouteIds = tourist.getSavedRoutes().stream()
                    .map(Route::getId)
                    .toList();
        }
    }
}