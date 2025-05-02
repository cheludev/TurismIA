package com.turismea.controller;

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
    private final CityService cityService;
    private final ReportService reportService;

    public SpotController(SpotService spotService, CityService cityService, ReportService reportService) {
        this.spotService = spotService;
        this.cityService = cityService;
        this.reportService = reportService;
    }

    @GetMapping("/")
    public ResponseEntity<?> listSpots(){
        List<Spot> spots =  spotService.getAllSpots();
        if(spots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "No spots created yet",
                            "body", List.of()
                    )
            );
        } else {
            List<SpotResponseDTO> spotResponse = spots.stream().map(SpotResponseDTO::new).toList();
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "List of spots",
                            "body", spotResponse
                    )
            );
        }
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<?> validateSpot(@PathVariable Long id){
        Optional<Spot> spotOptional = spotService.findById(id);
        Spot spot = new Spot();

        if(spotOptional.isPresent()) {
            spot = spotOptional.get();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "status", "error",
                            "message", "The spot does not exist"
                    )
            );
        }

        if(spot.isValidated()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "status", "error",
                            "message", "The spot is already validated"
                    )
            );
        } else {
            spotService.validateSpot(id);
            SpotResponseDTO spt = spotService.findById(id).map(SpotResponseDTO::new).get();
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "Spot has been validated",
                            "body", spt
                    )
            );
        }

    }

}
