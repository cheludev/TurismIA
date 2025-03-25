package com.turismea.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@Getter
public class Location {

    private Double latitude;
    private Double longitude;

}
