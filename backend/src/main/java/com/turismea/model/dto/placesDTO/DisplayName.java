package com.turismea.model.dto.placesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisplayName {

    @JsonProperty("text")
    private String text;

    @JsonProperty("languageCode")
    private String languageCode;

    public DisplayName() {}

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getLanguageCode() {
        return languageCode;
    }
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}

