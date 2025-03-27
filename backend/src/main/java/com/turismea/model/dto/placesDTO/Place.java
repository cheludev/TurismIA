package com.turismea.model.dto.placesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.turismea.model.dto.LocationDTO;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String placeId;

    @JsonProperty("formattedAddress")
    private String formattedAddress;

    @JsonProperty("location")
    private LocationDTO locationDTO;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("displayName")
    private DisplayName displayName;

    public String getName() {
        if (displayName != null && displayName.getText() != null) {
            return displayName.getText();
        }
        return name;
    }

}

