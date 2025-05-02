package com.turismea.controller;

import com.turismea.model.dto.RouteDTO.RouteRequestDTO;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.Route;
import com.turismea.service.RouteGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteGeneratorService routeGeneratorService;

    public RouteController(RouteGeneratorService routeGeneratorService) {
        this.routeGeneratorService = routeGeneratorService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> generateRoute(@RequestBody RouteRequestDTO routeRequest) {
        try {
            Route route = routeGeneratorService.generateRoute(
                    routeRequest.getFrom(),
                    routeRequest.getTo(),
                    routeRequest.getMaxDuration()
            );

            List<SpotResponseDTO> spotDTOs = route.getSpots().stream()
                    .map(SpotResponseDTO::new)
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "Route generated successfully",
                            "body", Map.of(
                                    "spots", spotDTOs,
                                    "totalDuration", route.getDuration()
                            )
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "status", "error",
                            "message", "Failed to generate route: " + e.getMessage()
                    )
            );
        }
    }


}
