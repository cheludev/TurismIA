package com.turismea.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {

    @JsonProperty("displayName.text")
    private DisplayName displayName;

    @JsonProperty("formattedAddress")
    private String address;

    @JsonProperty("location.latitude")
    private Double latitude;

    @JsonProperty("location.longitude")
    private Double longitude;

    @JsonProperty("id")
    private String placeId;

    public Place(DisplayName displayName, String formattedAddress, Location location, String id) {
        this.displayName = displayName;
        this.address = formattedAddress;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.placeId = id;
    }
    public Place() {}

    public String getName() { return displayName.getText(); }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }



}
