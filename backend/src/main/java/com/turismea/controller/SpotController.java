package com.turismea.controller;

import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.Spot;
import com.turismea.service.CityService;
import com.turismea.service.ReportService;
import com.turismea.service.SpotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;

    }

    @GetMapping("/")
    public ResponseEntity<?> listSpots() {
        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            return ApiResponseUtils.badRequest("No spots created yet");
        } else {
            List<SpotResponseDTO> spotResponse = spots.stream().map(SpotResponseDTO::new).toList();
            return ApiResponseUtils.success("List of spots", spotResponse);
        }
    }
    @GetMapping("/validated/{city}")
    public ResponseEntity<?> listSpots(@PathVariable("city") String city) {
        List<Spot> spots = spotService.getValidatedSpotByCity(city);

        if (spots.isEmpty()) {
            return ApiResponseUtils.badRequest("No spots created yet");
        } else {
            List<SpotResponseDTO> spotResponse = spots.stream().map(SpotResponseDTO::new).toList();
            return ApiResponseUtils.success("List of spots", spotResponse);
        }
    }


    @PutMapping("/{id}/validate")
    public ResponseEntity<?> validateSpot(@PathVariable Long id) {
        Optional<Spot> spotOptional = spotService.findById(id);

        if (spotOptional.isEmpty()) {
            return ApiResponseUtils.notFound("The spot does not exist");
        }

        Spot spot = spotOptional.get();

        if (spot.isValidated()) {
            return ApiResponseUtils.conflict("The spot is already validated");
        }

        spotService.validateSpot(id);
        SpotResponseDTO spt = spotService.findById(id).map(SpotResponseDTO::new).get();

        return ApiResponseUtils.success("Spot has been validated", spt);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSpot(@PathVariable("id") Long id) {
        Optional<Spot> optionalSpot = spotService.findById(id);
        if(optionalSpot.isPresent()) {
            spotService.deleteSpot(optionalSpot.get());
            return spotService.exitsById(id)?
                    ApiResponseUtils.success("Spot " + id + " has been deleted successfully")
                    :
                    ApiResponseUtils.internalServerError("An error was occurred trying to delete the spot");
        } else {
            return ApiResponseUtils.notFound("The spot was not found successfully");
        }
    }
}
