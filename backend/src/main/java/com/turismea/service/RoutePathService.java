package com.turismea.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.RoutePath;
import com.turismea.repository.RoutePathRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoutePathService {

    private final RoutePathRepository routePathRepository;
    private final ObjectMapper objectMapper;

    public RoutePathService(RoutePathRepository routePathRepository, ObjectMapper objectMapper) {
        this.routePathRepository = routePathRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<List<LocationDTO>> getPolylineForRoute(Route route) {
        return routePathRepository.findByRoute(route)
                .map(routePath -> {
                    try {
                        return List.of(objectMapper.readValue(routePath.getPolylineJson(), LocationDTO[].class));
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                });
    }

    public void savePolyline(Route route, List<LocationDTO> polyline) {
        try {
            String json = objectMapper.writeValueAsString(polyline);

            // Si ya existe una RoutePath para esta ruta, la reemplazamos
            RoutePath routePath = routePathRepository.findByRoute(route).orElse(new RoutePath());
            routePath.setRoute(route);
            routePath.setPolylineJson(json);

            routePathRepository.save(routePath);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("No se pudo serializar la polilínea a JSON", e);
        }
    }

    public void deletePolyline(Route route) {
        routePathRepository.deleteByRoute(route);
    }
}
