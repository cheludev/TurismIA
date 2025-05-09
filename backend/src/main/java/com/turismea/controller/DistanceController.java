package com.turismea.controller;

import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.entity.City;
import com.turismea.model.entity.CityDistance;
import com.turismea.repository.CityDistanceRepository;
import com.turismea.service.CityDistanceService;
import com.turismea.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/distances")
public class DistanceController {

    private final CityService cityService;
    private final CityDistanceRepository cityDistanceRepository;
    private final CityDistanceService cityDistanceService;

    public DistanceController(CityService cityService, CityDistanceRepository cityDistanceRepository, CityDistanceService cityDistanceService) {
        this.cityService = cityService;
        this.cityDistanceRepository = cityDistanceRepository;
        this.cityDistanceService = cityDistanceService;
    }

    @GetMapping("/city/test/{cityName}")
    public ResponseEntity<?> listDistances(@PathVariable String cityName) {
        City city = cityService.getCityByName(cityName)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada: " + cityName));

        List<CityDistance> distances = cityDistanceRepository.findAll()
                .stream()
                .filter(cd -> cd.getCity() != null && cd.getCity().getId().equals(city.getId()))
                .toList();

        List<Map<String, Object>> result = distances.stream().map(cd -> {
            Map<String, Object> map = new HashMap<>();
            map.put("spotA", cd.getSpotA().getName());
            map.put("spotB", cd.getSpotB().getName());
            map.put("distance", cd.getDistance());
            map.put("duration", cd.getDuration());
            map.put("hasGeometry", cd.getGeometryJson() != null && !cd.getGeometryJson().isEmpty());
            return map;
        }).toList();

        return ResponseEntity.ok(Map.of(
                "city", city.getName(),
                "totalDistances", distances.size(),
                "distances", result
        ));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/fill-missing-geometry")
    public ResponseEntity<?> fillMissingGeometries() {
        int updatedCount = cityDistanceService.fillMissingGeometries();
        return ApiResponseUtils.success("Filled " + updatedCount + " missing geometries");
    }
}
