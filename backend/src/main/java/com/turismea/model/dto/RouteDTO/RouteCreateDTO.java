package com.turismea.model.dto.RouteDTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class RouteCreateDTO {
    private String name;
    private Long cityId;
    private Long ownerId;
    private List<Long> spotIds;
    private int duration;
    private String description;
}
