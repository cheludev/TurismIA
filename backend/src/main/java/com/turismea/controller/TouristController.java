package com.turismea.controller;

import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.TouristDTO.TouristResponseDTO;
import com.turismea.model.entity.Tourist;
import com.turismea.service.TouristService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tourist")
public class TouristController {

    private final TouristService touristService;

    public TouristController(TouristService touristService) {
        this.touristService = touristService;
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





}
