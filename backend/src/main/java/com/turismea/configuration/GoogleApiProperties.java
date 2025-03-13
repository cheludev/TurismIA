package com.turismea.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.api")
public class GoogleApiProperties {
    private String baseUrl;
    private String key;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getKey() {
        return key;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
