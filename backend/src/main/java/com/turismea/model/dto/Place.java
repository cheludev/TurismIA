package com.turismea.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("displayName")
    private DisplayName displayName;

    public Place() {}

    public String getName() {
        if (displayName != null && displayName.getText() != null) {
            return displayName.getText();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }
    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }
}

