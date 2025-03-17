package com.turismea.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Spot;
import com.turismea.repository.CityRepository;
import com.turismea.repository.SpotRepository;
import com.turismea.exception.SpotNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import io.reactivex.rxjava3.core.Observable;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class SpotServiceTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private GoogleSpotService googleSpotService;

    @Mock
    private CityService cityService;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private SpotService spotService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String mockJsonResponse = "{ \"places\": [" +
            "{ \"displayName\": { \"text\": \"Museo de Huelva\" }, " +
            "\"formattedAddress\": \"Huelva, España\", " +
            "\"location\": { \"latitude\": 37.2583, \"longitude\": -6.9495 }, " +
            "\"id\": \"ChIJr8h3sHtxEg0Rn1w9QhDys4E\" } ] }";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateSpot() {
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setValidated(false);

        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));

        spotService.validateSpot(spot);

        assertTrue(spot.isValidated());
        verify(spotRepository).save(spot);
    }

    @Test
    void testValidateSpot_NotExist() {
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setValidated(false);

        when(spotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SpotNotFoundException.class, () -> spotService.validateSpot(spot));

        verify(spotRepository, never()).save(any(Spot.class));
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





    @Test
    void testSaveCitySpots() throws Exception {
        String cityName = "Madrid";

        String fakeJsonResponse = """
            {
              "places": [
                {
                  "name": "places/ChIJSyhE1-bPEQ0Rgw-0EMGqK9g",
                  "id": "ChIJSyhE1-bPEQ0Rgw-0EMGqK9g",
                  "formattedAddress": "Madrid, Spain",
                  "location": {
                    "latitude": 40.4168,
                    "longitude": -3.7038
                  },
                  "displayName": {
                    "text": "Huelva",
                    "languageCode": "en"
                  }
                }
              ]
            }
            """;

        when(googleSpotService.getSpots(cityName))
                .thenReturn(Mono.just(fakeJsonResponse));

        City mockCity = new City();
        mockCity.setName(cityName);
        when(cityService.findByName(cityName)).thenReturn(Optional.of(mockCity));

        spotService.saveCitySpots(cityName);

        ArgumentCaptor<Spot> spotCaptor = ArgumentCaptor.forClass(Spot.class);
        verify(spotRepository).save(spotCaptor.capture());
        Spot savedSpot = spotCaptor.getValue();

        assertEquals("Madrid", savedSpot.getCity().getName());


        assertEquals("Huelva", savedSpot.getName());

        assertEquals("Madrid, Spain", savedSpot.getAddress());
        assertEquals(40.4168, savedSpot.getLatitude());
        assertEquals(-3.7038, savedSpot.getLongitude());
    }
}
