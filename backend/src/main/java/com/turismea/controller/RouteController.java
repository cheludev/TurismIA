package com.turismea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.exception.CityNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.LocationDTO;
import com.turismea.model.dto.RouteDTO.*;
import com.turismea.model.dto.SpotDTO.SpotIdListDTO;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.*;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.RouteRepository;
import com.turismea.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;
    private final RoutePathService routePathService;

    public RouteController(RouteGeneratorService routeGeneratorService, TouristService touristService,
                           SpotService spotService, RouteService routeService, CityService cityService,
                           UserService userService, RoutePathService routePathService) {
        this.routeGeneratorService = routeGeneratorService;
        this.cityService = cityService;
        this.touristService = touristService;
        this.spotService = spotService;
        this.routeService = routeService;
        this.userService = userService;
        this.routePathService = routePathService; // ✅ Añadido
    }

    @PreAuthorize("hasRole('TOURIST')")
    @PostMapping("/new")
    public ResponseEntity<?> generateRoute(@RequestBody RouteRequestDTO routeRequest) {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Usuario logueado: " + auth.getName() + " Roles: " + auth.getAuthorities());

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
            System.out.println(new ObjectMapper().writeValueAsString(body));

            return ApiResponseUtils.success("Route generated successfully", body);

        } catch (Exception e) {
            return ApiResponseUtils.badRequest("Failed to generate route: " + e.getMessage());
        }
    }


    // 🔵 CREAR RUTA
    @PostMapping("/")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<ApiResponse<RouteResponseDTO>> createRoute(@RequestBody RouteCreateDTO dto) {

        User authUser = userService.getUserFromAuth();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Usuario logueado: " + auth.getName() + " Roles: " + auth.getAuthorities());

        if (!authUser.getId().equals(dto.getOwnerId()) && !authUser.getRole().equals(Role.ADMIN)) {
            return ApiResponseUtils.success("You can't create routes for another user", null);
        }

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
        route.setDuration(routeService.calculateDurationOfARoute(spots));
        route.setDraft(true);

        Route savedRoute = routeService.save(route);

        // ✅ Calcular y guardar polyline
        List<LocationDTO> polyline = routeService.buildPolyline(savedRoute);
        routePathService.savePolyline(savedRoute, polyline);

        RouteResponseDTO dtoResponse = new RouteResponseDTO(savedRoute);
        dtoResponse.setPolyline(polyline);

        return ApiResponseUtils.success(
                "Route created successfully",
                dtoResponse
        );
    }

    // 🔵 GET RUTA (obtener ruta)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<?> getRoute(@PathVariable("id") Long id) {
        Route route = routeService.getRouteWithSpotsById(id);

        RouteResponseDTO dto = new RouteResponseDTO(route);

        // ✅ Leer polyline ya guardada
        Optional<List<LocationDTO>> polylineOpt = routePathService.getPolylineForRoute(route);

        if (polylineOpt.isPresent()) {
            dto.setPolyline(polylineOpt.get());
        } else {
            dto.setPolyline(List.of()); // Si no hay, devolver lista vacía
        }

        return ApiResponseUtils.success("Route", dto);
    }

    // 🔵 EDITAR RUTA
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<?> editRoute(@PathVariable("id") Long id, @RequestBody RouteResponseDTO dto) {

        User authUser = userService.getUserFromAuth();

        Route route = routeService.getRouteWithSpotsById(id);

        if (!authUser.getId().equals(route.getOwner().getId()) && !authUser.getRole().equals(Role.ADMIN)) {
            return ApiResponseUtils.unauthorized("You can't edit another user's route");
        }

        City city = cityService.getCityById(dto.getCityId())
                .orElseThrow(() -> new CityNotFoundException(dto.getCityId()));

        Tourist owner = touristService.getTouristById(dto.getOwnerId());

        List<Spot> spots = spotService.findAllById(dto.getSpotIds());

        route.setName(dto.getName());
        route.setCity(city);
        route.setOwner(owner);
        route.setSpots(new ArrayList<>(spots));
        route.setDescription(dto.getDescription());
        route.setRate(routeService.calculateRatingOfARoute(route.getSpots()));
        route.setDuration(routeService.calculateDurationOfARoute(spots));
        route.setDraft(true);

        Route savedRoute = routeService.save(route);

        // ✅ Calcular y guardar polyline nueva (porque cambiaron los spots)
        List<LocationDTO> polyline = routeService.buildPolyline(savedRoute);
        routePathService.savePolyline(savedRoute, polyline);

        RouteResponseDTO dtoResponse = new RouteResponseDTO(savedRoute);
        dtoResponse.setPolyline(polyline);

        return ApiResponseUtils.success(
                "Route edited successfully",
                dtoResponse
        );
    }


    @PostMapping("/{id}/rate")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<?> rateRoute(@PathVariable Long id, @RequestParam double rate) {
        User authUser = userService.getUserFromAuth();
        Route route = routeService.getRouteWithSpotsById(id);
        // Aquí podrías llevar un promedio o solo permitir un rating por usuario
        route.setRate(rate); // Simplificación
        routeService.save(route);
        return ApiResponseUtils.success("Ruta valorada correctamente");
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

    @GetMapping("/city/name/{cityName}")
    public ResponseEntity<?> getRoutesByCityName(@PathVariable String cityName) {
        List<Route> routes = routeService.getRoutesByCityName(cityName);

        if (routes.isEmpty()) {
            return ApiResponseUtils.notFound("No routes found for the city: " + cityName);
        }

        List<RouteListDTO> routeResponseDTO = routes.stream()
                .map(RouteListDTO::new)
                .toList();

        return ApiResponseUtils.success("List of routes for the city: " + cityName, routeResponseDTO);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<?> publishRoute(@PathVariable("id") Long id) {

        User authUser = userService.getUserFromAuth();

        Route route = routeService.getRouteWithSpotsById(id);

        if (!authUser.getId().equals(route.getOwner().getId()) && !authUser.getRole().equals(Role.ADMIN)) {
            return ApiResponseUtils.unauthorized("You can't publish another user's route");
        }

        if (!route.isDraft()) {
            return ApiResponseUtils.success(
                    "Route is already published",
                    new RouteResponseDTO(route)
            );
        }

        route.setDraft(false);
        Route savedRoute = routeService.save(route);

        return ApiResponseUtils.success(
                "Route published successfully",
                new RouteResponseDTO(savedRoute)
        );
    }

    @PostMapping("/calculate-duration")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<?> calculateRouteDuration(@RequestBody SpotIdListDTO dto) {
        try {
            long duration = routeGeneratorService.calculateTotalRouteDurationFromIds(dto.getSpotIds());
            Map<String, Object> result = Map.of("totalDuration", duration);
            return ApiResponseUtils.success("Route duration calculated successfully", result);
        } catch (Exception e) {
            return ApiResponseUtils.badRequest("Failed to calculate duration: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TOURIST') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoute(@PathVariable("id") Long id) {

        User authUser = userService.getUserFromAuth();

        Route route = routeService.getRouteWithSpotsById(id);

        if (!authUser.getId().equals(route.getOwner().getId()) && !authUser.getRole().equals(Role.ADMIN)) {
            return ApiResponseUtils.unauthorized("You can't delete another user's route");
        }

        routeService.delete(route);

        return ApiResponseUtils.success("Route deleted successfully");
    }
}
