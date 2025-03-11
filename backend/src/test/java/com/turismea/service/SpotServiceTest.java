package com.turismea.service;

import com.turismea.model.City;
import com.turismea.model.Spot;
import com.turismea.repository.SpotRepository;
import com.turismea.exception.SpotNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class SpotServiceTest {

    @Mock
    private SpotRepository spotRepository;

    @InjectMocks
    private SpotService spotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateSpot() {
        Spot spot = new Spot();
        spot.setValidated(false);

        spotService.validateSpot(spot);

        assertTrue(spot.isValidated());
        verify(spotRepository).save(spot);
    }

    @Test
    void testNewTouristicSpot_SpotExists() {
        Long locationId = 1L;
        String touristicInfo = "New touristic information";

        Spot fakeSpot = new Spot();
        fakeSpot.setId(locationId);
        fakeSpot.setValidated(false);
        fakeSpot.setInfo("Old information");

        when(spotRepository.findById(locationId)).thenReturn(Optional.of(fakeSpot));

        Spot updatedSpot = spotService.newTouristicSpot(locationId, touristicInfo);

        assertTrue(updatedSpot.isValidated());
        assertEquals(touristicInfo, updatedSpot.getInfo());

        verify(spotRepository).findById(locationId);
    }

    @Test
    void testNewTouristicSpot_SpotNotFound() {
        Long locationId = 1L;
        when(spotRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(SpotNotFoundException.class, () ->
                spotService.newTouristicSpot(locationId, "Touristic info"));

        verify(spotRepository).findById(locationId);
    }

    @Test
    void testGetValidatedSpotByCity() {
        City fakeCity = new City();
        fakeCity.setId(1L);
        fakeCity.setName("Test City");

        Spot validatedSpot1 = new Spot();
        validatedSpot1.setValidated(true);

        Spot validatedSpot2 = new Spot();
        validatedSpot2.setValidated(true);

        List<Spot> validatedSpots = Arrays.asList(validatedSpot1, validatedSpot2);

        when(spotRepository.getSpotByValidatedAndCity(true, fakeCity)).thenReturn(validatedSpots);

        List<Spot> result = spotService.getValidatedSpotByCity(fakeCity);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isValidated());
        assertTrue(result.get(1).isValidated());

        verify(spotRepository).getSpotByValidatedAndCity(true, fakeCity);
    }

    @Test
    void testGetAllSpotByCity() {
        City fakeCity = new City();
        fakeCity.setId(1L);
        fakeCity.setName("Test City");

        Spot spot1 = new Spot();
        Spot spot2 = new Spot();

        List<Spot> allSpots = Arrays.asList(spot1, spot2);

        when(spotRepository.getSpotByCity(fakeCity)).thenReturn(allSpots);

        List<Spot> result = spotService.getAllSpotByCity(fakeCity);

        assertEquals(2, result.size());

        verify(spotRepository).getSpotByCity(fakeCity);
    }
}
