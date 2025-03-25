package com.turismea.model.dto.placesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Location location;

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

