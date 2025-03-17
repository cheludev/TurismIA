package com.turismea.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.turismea.exception.CityNotFoundException;
import com.turismea.model.entity.City;
import com.turismea.repository.CityRepository;


class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteCity_Success() {
        Long cityId = 1L;
        City city = new City();
        city.setId(cityId);
        city.setName("TestCity");

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        doNothing().when(cityRepository).delete(city);

        assertDoesNotThrow(() -> cityService.deleteCity(cityId));
        verify(cityRepository).findById(cityId);
        verify(cityRepository).delete(city);
    }

    @Test
    void testDeleteCity_CityNotFound() {
        Long cityId = 1L;
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> cityService.deleteCity(cityId));
        verify(cityRepository).findById(cityId);
        verify(cityRepository, never()).delete(any(City.class));
    }

    @Test
    void testDeleteCity_NullId() {
        assertThrows(IllegalArgumentException.class, () -> cityService.deleteCity(null));
        verify(cityRepository, never()).findById(any());
        verify(cityRepository, never()).delete(any());
    }

    @Test
    void testFindByName_Success() {
        String cityName = "Barcelona";
        City city = new City();
        city.setName(cityName);

        when(cityRepository.findByName(cityName)).thenReturn(city);

        City foundCity = cityService.findByName(cityName).orElseThrow();

        assertNotNull(foundCity);
        assertEquals(cityName, foundCity.getName());
        verify(cityRepository).findByName(cityName);
    }
}





