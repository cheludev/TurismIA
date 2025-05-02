package com.turismea.model.dto.SpotDTO;

import com.turismea.model.entity.Spot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotResponseDTO {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private int averageTime;
    private boolean validated;
    private String info;

    public SpotResponseDTO(Spot spot) {
        this.id = spot.getId();
        this.name = spot.getName();
        this.address = spot.getAddress();
        this.latitude = spot.getLatitude();
        this.longitude = spot.getLongitude();
        this.rating = spot.getRating();
        this.averageTime = spot.getAverageTime();
        this.validated = spot.isValidated();
        this.info = spot.getInfo();
    }
}
