package com.turismea.controller;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.RouteDTO.RouteListDTO;
import com.turismea.model.dto.RouteDTO.RouteResponseDTO;
import com.turismea.model.dto.TouristDTO.TouristResponseDTO;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.RouteRepository;
import com.turismea.service.RouteService;
import com.turismea.service.TouristService;
import com.turismea.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tourists")
public class TouristController {

    private final TouristService touristService;
    private final RouteService routeService;
    private final UserService userService;
    private final RouteRepository routeRepository;

    public TouristController(TouristService touristService, RouteService routeService, UserService userService, RouteRepository routeRepository) {
        this.touristService = touristService;
        this.routeService = routeService;
        this.userService = userService;
        this.routeRepository = routeRepository;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") Long id) {

        Optional<Tourist> touristOptional = touristService.findById(id);
        if (touristOptional.isPresent()) {
            Tourist tourist = touristOptional.get();
            TouristResponseDTO dto = new TouristResponseDTO(tourist);
            return ApiResponseUtils.success(
                    "User found successfully", dto);
        } else {
            return ApiResponseUtils.notFound(
                    "User with id " + id + " does not exist in the database");
        }
    }

    @GetMapping("/{userId}/drafts")
    public ResponseEntity<?> getDraftsRoutes(@PathVariable("userId") Long userId) {
        User authUser = userService.getUserFromAuth();
        if (!authUser.getId().equals(userId)) {
            return ApiResponseUtils.unauthorized("You can't access other user's data");
        }
        List<Route> drafts = routeService.getDraftsOfAnUser(userId);

        if (drafts.isEmpty()) {
            return ApiResponseUtils.notFound("No draft routes found for user " + userId);
        }

        List<RouteListDTO> responseDTO = drafts.stream()
                .map(RouteListDTO::new)
                .toList();

        return ApiResponseUtils.success(
                "List of draft routes for user " + userId,
                responseDTO
        );
    }


    @PostMapping("/routes/{routeId}/save")
    public ResponseEntity<?> saveRouteForUser(@PathVariable("routeId") Long routeId) {
        User authUser = userService.getUserFromAuth();

        Route route = routeService.getRouteWithSpotsById(routeId);

        if (!authUser.getRole().equals(Role.TOURIST)) {
            return ApiResponseUtils.unauthorized("Only TOURIST users can save routes");
        }

        Tourist tourist = touristService.findByIdWithSavedRoutes(authUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Tourist user not found"));

        if (!tourist.getSavedRoutes().contains(route)) {
            tourist.getSavedRoutes().add(route);
            touristService.save(tourist);
        }

        return ApiResponseUtils.success("Route " + routeId + " saved successfully for user " + authUser.getUsername());
    }

    @GetMapping("/{id}/saved")
    public ResponseEntity<?> getSavedRoutes(@PathVariable("id") Long userId) {
        User authUser = userService.getUserFromAuth();
        if (!authUser.getId().equals(userId)) {
            return ApiResponseUtils.unauthorized("You can't access other user's data");
        }
        List<Route> saved = routeService.getSavedRoutes(userId);

        if (saved.isEmpty()) {
            return ApiResponseUtils.notFound("No draft routes found for user " + userId);
        }

        List<RouteListDTO> responseDTO = saved.stream()
                .map(RouteListDTO::new)
                .toList();

        return ApiResponseUtils.success(
                "List of saved routes for user " + userId,
                responseDTO
        );
    }
    @DeleteMapping("/{userId}/routes/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable("userId") Long userId, @PathVariable("routeId") Long routeId) {

        User authUser = userService.getUserFromAuth();
        if (!authUser.getId().equals(userId)) {
            return ApiResponseUtils.unauthorized("You can't delete another user's routes");
        }

        Route route = routeService.getRouteWithSpotsById(routeId);

        boolean isOwner = route.getOwner().getId().equals(userId);

        if (isOwner) {

            List<Tourist> allTourists = touristService.findAllTourists();

            for (Tourist tourist : allTourists) {
                if (tourist.getSavedRoutes().contains(route)) {
                    tourist.getSavedRoutes().remove(route);
                    touristService.save(tourist);
                }
            }

            routeService.delete(route);

            return ApiResponseUtils.success("Route " + routeId + " deleted successfully from the platform");

        } else {
            Tourist tourist = touristService.findByIdWithSavedRoutes(userId)
                    .orElseThrow(() -> new UserNotFoundException("Tourist user not found"));

            if (tourist.getSavedRoutes().contains(route)) {
                tourist.getSavedRoutes().remove(route);
                touristService.save(tourist);

                return ApiResponseUtils.success("Route " + routeId + " removed from your saved routes");
            } else {
                return ApiResponseUtils.notFound("Route " + routeId + " is not in your saved routes");
            }
        }
    }




}
