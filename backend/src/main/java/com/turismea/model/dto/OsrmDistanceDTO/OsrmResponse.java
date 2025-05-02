package com.turismea.model.dto.OsrmDistanceDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class OsrmResponse {

    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "routes")
    private List<RouteDTO> routes;


    public OsrmResponse(String code, List<RouteDTO> routes) {
        this.code = code;
        this.routes = routes;
    }

}
