package com.turismea.model.dto.CityDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
public class CityDTO {
    @NotBlank(message = "The city name is required")
    private String name;
}
