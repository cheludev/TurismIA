package com.turismea.controller;

import com.turismea.exception.CityNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.PlacesDTO.Point;
import com.turismea.model.dto.SpotDTO.SpotResponseDTO;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/spots")
public class SpotController {

    private final SpotService spotService;
    private final CityService cityService;
    @Autowired
    private final GoogleSpotService googleSpotService;
    @Autowired
    private CityDistanceService cityDistanceService;

    public SpotController(SpotService spotService, CityService cityService, GoogleSpotService googleSpotService) {
        this.spotService = spotService;
        this.cityService = cityService;
        this.googleSpotService = googleSpotService;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/validated/{city}")
    public ResponseEntity<?> listSpots(@PathVariable("city") String city) {

        if(cityService.findByName(city).isPresent()){
            List<Spot> spots = spotService.getValidatedSpotByCity(city);

            if (spots.isEmpty()) {
                return ApiResponseUtils.badRequest("No spots created yet");
            } else {
                List<SpotResponseDTO> spotResponse = spots.stream().map(SpotResponseDTO::new).toList();
                return ApiResponseUtils.success("List of spots", spotResponse);
            }
        } else {
            return ApiResponseUtils.badRequest("City does not exist");
        }

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/allIn/{cityName}")
    public ResponseEntity<?> listValidatedSpots_ToUserByCity(@PathVariable("cityName") String city) {

        if(cityService.findByName(city).isPresent()){
            List<Spot> spots = spotService.getSpotsByCityName(city);

            if (spots.isEmpty()) {
                return ApiResponseUtils.badRequest("No spots created yet");
            } else {
                List<SpotResponseDTO> spotResponse = spots.stream().map(SpotResponseDTO::new).toList();
                return ApiResponseUtils.success("List of spots", spotResponse);
            }
        } else {
            return ApiResponseUtils.badRequest("City does not exist");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
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



    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpotById(@PathVariable("id") Long id) {
        Optional<Spot> spotOptional = spotService.findById(id);

        if (spotOptional.isPresent()) {
            SpotResponseDTO spotDTO = new SpotResponseDTO(spotOptional.get());
            return ApiResponseUtils.success("Spot found successfully", spotDTO);
        } else {
            return ApiResponseUtils.notFound("Spot with id " + id + " not found");
        }
    }

    @GetMapping("/location/by-name")
    public Mono<ResponseEntity<ApiResponse<Point>>> getLocationByName(@RequestParam("name") String locationName) {
        return googleSpotService.getSpotByName(locationName)
                .map(point -> ApiResponseUtils.success("Location found", point))
                .onErrorResume(e -> Mono.just(ApiResponseUtils.badRequest("Could not find location: " + e.getMessage())));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping("/import/{city}")
    public ResponseEntity<?> importAndSaveSpotsFromGoogle(@PathVariable("city") String cityName) {

        // 1️⃣ Importar y guardar spots
        spotService.saveCitySpots(cityName).subscribe();

        // 2️⃣ Recalcular CityDistances
        City city = cityService.getCityByName(cityName)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada: " + cityName));

        List<Spot> spots = spotService.getSpotsByCity(city);
        cityDistanceService.getAllDistances(city, spots);

        return ApiResponseUtils.success(
                "Importación y guardado ejecutados para la ciudad: " + cityName + " y CityDistances recalculados."
        );
    }





    @GetMapping("/unvalidated/{city}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUnvalidatedSpots(@PathVariable String city) {
        City c = cityService.getCityByName(city)
                .orElseThrow(() -> new CityNotFoundException(city));
        List<Spot> unvalidated = spotService.getUnValidatedSpotByCity(c);
        return ApiResponseUtils.success("Not validated Spots", unvalidated);
    }

}
