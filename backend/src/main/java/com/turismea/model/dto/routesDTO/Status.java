package com.turismea.model.dto.routesDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private int code;
    private String status;
    private Details details;
}
