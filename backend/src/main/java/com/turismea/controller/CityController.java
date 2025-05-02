package com.turismea.controller;

import com.turismea.model.dto.CityDTO.CityDTO;
import com.turismea.model.dto.CityDTO.CityResponseDTO;
import com.turismea.model.entity.City;
import com.turismea.repository.CityRepository;
import com.turismea.service.CityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityRepository cityRepository, CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createCity(@Valid @RequestBody CityDTO newCityDTO) {
        if(cityService.getCityByName(newCityDTO.getName()).isPresent()){
            City existingCity = cityService.getCityByName(newCityDTO.getName()).get();
            CityResponseDTO responseDTO = new CityResponseDTO(existingCity.getId(), existingCity.getName());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "status", "error",
                            "message", "The city you trying to create already exists",
                            "body", responseDTO
                    )
            );
        } else {
            City city = new City(newCityDTO.getName());
            City createdCity = cityService.save(city);
            CityResponseDTO responseDTO = new CityResponseDTO(createdCity.getId(), createdCity.getName());

            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "The city has been created successfully",
                            "body", responseDTO
                    )
            );
        }
    }


    @GetMapping("/{name}")
    public ResponseEntity<?> searchCity(@PathVariable String name) {
        var cityOptional = cityService.getCityByName(name);

        if (cityOptional.isPresent()) {
            City city = cityOptional.get();
            CityResponseDTO responseDTO = new CityResponseDTO(city.getId(), city.getName());

            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of(
                            "status", "success",
                            "message", "",
                            "body", responseDTO
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "status", "error",
                            "message", "The city you trying to find does not exist"
                    )
            );
        }
    }


}
