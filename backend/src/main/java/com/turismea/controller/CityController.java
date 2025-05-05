package com.turismea.controller;

import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
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
import java.util.Optional;

@RestController
@RequestMapping("api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityRepository cityRepository, CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createCity(@Valid @RequestBody CityDTO newCityDTO) {
        Optional<City> existingCityOpt = cityService.getCityByName(newCityDTO.getName());

        if (existingCityOpt.isPresent()) {
            return ApiResponseUtils.conflict("The city you are trying to create already exists");
        }

        City city = new City(newCityDTO.getName());
        City createdCity = cityService.save(city);
        CityResponseDTO responseDTO = new CityResponseDTO(
                createdCity.getId(),
                createdCity.getName()
        );

        return ApiResponseUtils.success("The city has been created successfully", responseDTO);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> searchCity(@PathVariable String name) {
        Optional<City> cityOptional = cityService.getCityByName(name);

        if (cityOptional.isPresent()) {
            City city = cityOptional.get();
            CityResponseDTO responseDTO = new CityResponseDTO(city.getId(), city.getName());
            return ApiResponseUtils.success("", responseDTO);
        } else {
            return ApiResponseUtils.notFound(
                    "The city you are trying to find does not exist");
        }
    }


}
