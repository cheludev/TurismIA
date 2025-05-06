package com.turismea.controller;

import com.turismea.exception.CityNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.RouteDTO.*;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.*;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.RouteRepository;
import com.turismea.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteGeneratorService routeGeneratorService;
    private final CityService cityService;
    private final TouristService touristService;
    private final SpotService spotService;
    private final RouteService routeService;
    private final RouteRepository routeRepository;
    private final UserService userService;

    public RouteController(RouteGeneratorService routeGeneratorService, TouristService touristService,
                           SpotService spotService, RouteService routeService, CityService cityService, RouteRepository routeRepository, UserService userService) {
        this.routeGeneratorService = routeGeneratorService;
        this.cityService = cityService;
        this.touristService = touristService;
        this.spotService = spotService;
        this.routeService = routeService;
        this.routeRepository = routeRepository;
        this.userService = userService;
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

        City city = cityService.getCityById(dto.getCityId())
                .orElseThrow(() -> new CityNotFoundException(dto.getCityId()));
        Tourist owner = touristService.getTouristById(dto.getOwnerId());

        List<Spot> spots = spotService.findAllById(dto.getSpotIds());

        Route route = new Route();
        route.setName(dto.getName());
        route.setCity(city);
        route.setOwner(owner);
        route.setSpots(new LinkedList<>(spots));
        route.setDescription(dto.getDescription());
        route.setRate(routeService.calculateRatingOfARoute(route.getSpots()));
        route.setDuration(dto.getDuration());
        route.setDraft(true);

        Route savedRoute = routeService.save(route);
        return ApiResponseUtils.success(
                "Route created successfully",
                new RouteResponseDTO(savedRoute)
        );

    }

    @GetMapping("/")
    public ResponseEntity<?> getRoutes() {
        List<Route> routes = routeService.getAllRoutes();

        if (routes.isEmpty()) {
            return ApiResponseUtils.notFound("No routes have been created yet");
        }

        List<RouteListDTO> routeResponseDTO = routes.stream()
                .map(RouteListDTO::new)
                .toList();

        return ApiResponseUtils.success("List of all routes", routeResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoute(@PathVariable("id") Long id) {
        Route route = routeService.getRouteWithSpotsById(id);
        RouteResponseDTO dto = new RouteResponseDTO(route);
        return ApiResponseUtils.success("Route", dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editRoute(@PathVariable("id") Long id, @RequestBody RouteResponseDTO dto) {

        City city = cityService.getCityById(dto.getCityId())
                .orElseThrow(() -> new CityNotFoundException(dto.getCityId()));
        Tourist owner = touristService.getTouristById(dto.getOwnerId());
        List<Spot> spots = spotService.findAllById(dto.getSpotIds());

        Route route = routeService.getRouteWithSpotsById(id);

        route.setName(dto.getName());
        route.setCity(city);
        route.setOwner(owner);
        route.setSpots(new ArrayList<>(spots));
        route.setDescription(dto.getDescription());
        route.setRate(routeService.calculateRatingOfARoute(route.getSpots()));
        route.setDuration(routeService.calculateDurationOfARoute(spots));
        route.setDraft(true);

        Route savedRoute = routeService.save(route);
        Route fullRoute = routeService.getRouteWithSpotsById(savedRoute.getId());
        fullRoute.getSpots().size();

        return ApiResponseUtils.success(
                "Route edited successfully",
                new RouteResponseDTO(fullRoute)
        );
    }


    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishRoute(@PathVariable("id") Long id) {

        Route route = routeService.getRouteWithSpotsById(id);

        if (!route.isDraft()) {
            Route fullRoute = routeService.getRouteWithSpotsById(route.getId());
            return ApiResponseUtils.success(
                    "Route is already published",
                    new RouteResponseDTO(fullRoute)
            );
        }

        route.setDraft(false);

        Route savedRoute = routeService.save(route);
        Route fullRoute = routeService.getRouteWithSpotsById(savedRoute.getId()); // OJO: usar con spots

        return ApiResponseUtils.success(
                "Route published successfully",
                new RouteResponseDTO(fullRoute)
        );
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable("id") Long id) {
        User user = userService.getUserFromAuth();
        Optional<Route> optionalRoute = routeRepository.findByIdAndOwner_Id(id, user.getId());

        if (optionalRoute.isPresent()) {
            routeService.deleteRoute(id);
            return ApiResponseUtils.success("Route deleted satisfactory");
        } else {
            return ApiResponseUtils.badRequest("Route not found or does not belong to current user.");
        }
    }






}
