package com.turismea.controller;

import com.turismea.exception.CityNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.RouteDTO.RouteCreateDTO;
import com.turismea.model.dto.RouteDTO.RouteListDTO;
import com.turismea.model.dto.RouteDTO.RouteRequestDTO;
import com.turismea.model.dto.RouteDTO.RouteResponseDTO;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import com.turismea.model.entity.Tourist;
import com.turismea.repository.CityRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.SpotRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.service.RouteGeneratorService;
import com.turismea.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteGeneratorService routeGeneratorService;
    private final CityRepository cityRepository;
    private final TouristRepository touristRepository;
    private final SpotRepository spotRepository;
    private final RouteRepository routeRepository;
    private final RouteService routeService;

    public RouteController(RouteGeneratorService routeGeneratorService, CityRepository cityRepository, TouristRepository touristRepository, SpotRepository spotRepository, RouteRepository routeRepository, RouteService routeService) {
        this.routeGeneratorService = routeGeneratorService;
        this.cityRepository = cityRepository;
        this.touristRepository = touristRepository;
        this.spotRepository = spotRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
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

            Map<String, Object> body = Map.of(
                    "spots", spotDTOs,
                    "totalDuration", route.getDuration()
            );

            return ApiResponseUtils.success("Route generated successfully", body);

        } catch (Exception e) {
            return ApiResponseUtils.badRequest("Failed to generate route: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<RouteResponseDTO>> createRoute(@RequestBody RouteCreateDTO dto) {

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new CityNotFoundException(dto.getCityId()));
        Tourist owner = touristRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(dto.getOwnerId()));

        List<Spot> spots = spotRepository.findAllById(dto.getSpotIds());

        Route route = new Route();
        route.setName(dto.getName());
        route.setCity(city);
        route.setOwner(owner);
        route.setSpots(new LinkedList<>(spots));
        route.setDescription(dto.getDescription());
        route.setRate(routeService.calculateRatingFormARoute(route.getSpots()));
        route.setDuration(dto.getDuration());

        Route savedRoute = routeRepository.save(route);
        return ApiResponseUtils.success(
                "Route created successfully",
                new RouteResponseDTO(savedRoute)
        );

    }

    @GetMapping("/")
    public ResponseEntity<?> getRoutes(){
        List<Route> routes = routeService.getAllRoutes();

        if (routes.isEmpty()) {
            return ApiResponseUtils.notFound("No routes have been created yet");
        }

        List<RouteListDTO> routeResponseDTO = routes.stream()
                .map(RouteListDTO::new)
                .toList();

        return ApiResponseUtils.success("List of all routes", routeResponseDTO);
    }



}
