package com.turismea.controller;

import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.RouteDTO.RouteListDTO;
import com.turismea.model.dto.RouteDTO.RouteResponseDTO;
import com.turismea.model.dto.TouristDTO.TouristResponseDTO;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.service.RouteService;
import com.turismea.service.TouristService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tourist")
public class TouristController {

    private final TouristService touristService;
    private final RouteService routeService;

    public TouristController(TouristService touristService, RouteService routeService) {
        this.touristService = touristService;
        this.routeService = routeService;
    }

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

    @GetMapping("/drafts/{userId}")
    public ResponseEntity<?> getDraftRoutesByUser(@PathVariable("userId") Long userId) {

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

}
