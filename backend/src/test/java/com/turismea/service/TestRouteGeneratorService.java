package com.turismea.service;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.Spot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@ExtendWith(MockitoExtension.class)

public class TestRouteGeneratorService {


    @InjectMocks
    private RouteGeneratorService routeGeneratorService;

    @Mock
    private SpotService spotService;

    @Mock
    private  OrdenationAlgorithimService ordenationAlgorithimService;

    @Mock
    private  OpenStreetMapService openStreetMapService;

    @Mock
    private  CityDistanceService cityDistanceService;


    @Test
    void testGetBetterInitialPoint_ReturnsBestSpot() {
        // Arrange - define input data and mock behavior
        LocationDTO locationDTO = new LocationDTO(37.2614, -6.9447); // Huelva city center
        String expectedWkt = "POINT(-6.944700 37.261400)";
        double radius = 100.0;
        double maxRadius = 200.0;

        // Mock two nearby spots
        Spot spot1 = new Spot();
        spot1.setId(1L);
        spot1.setName("Muelle del Tinto");
        spot1.setLatitude(37.2610);
        spot1.setLongitude(-6.9480);

        Spot spot2 = new Spot();
        spot2.setId(2L);
        spot2.setName("Plaza de las Monjas");
        spot2.setLatitude(37.2605);
        spot2.setLongitude(-6.9390);

        List<Spot> mockSpots = new ArrayList<>(List.of(spot1, spot2));

        // Simulate spot service returning nearby spots
        when(spotService.getNearbySpotsToFromAPoint(anyString(), eq(radius))).thenReturn(mockSpots);

        // Simulate distance calculation returning same value for simplicity
        when(cityDistanceService.getDistancesBetween(any(), any())).thenReturn(400L);

        // Simulate sorting algorithm putting spot2 first
        doAnswer(invocation -> {
            List<Spot> spots = invocation.getArgument(0);
            Collections.swap(spots, 0, 1);
            return null;
        }).when(ordenationAlgorithimService).sortByDurationAndRating(anyList(), anyList());


        // Act - call the method under test
        Spot result = routeGeneratorService.getBetterNearestPoint(locationDTO, radius, maxRadius);

        // Assert - verify that the best spot is returned
        assertNotNull(result);
        assertEquals(spot2.getId(), result.getId());
    }

    @Test
    void testGetBetterInitialPoint_NoSpotsFound_ReturnsNull() {
        // Arrange - define input and mock empty responses
        LocationDTO locationDTO = new LocationDTO(37.2614, -6.9447);
        String expectedWkt = "POINT(-6,944700 37,261400)";
        double radius = 100.0;
        double maxRadius = 150.0;

        // Simulate no spots found within initial or increased radius
        when(spotService.getNearbySpotsToFromAPoint(expectedWkt, radius)).thenReturn(Collections.emptyList());
        when(spotService.getNearbySpotsToFromAPoint(expectedWkt, radius + 50)).thenReturn(Collections.emptyList());

        // Act
        Spot result = routeGeneratorService.getBetterNearestPoint(locationDTO, radius, maxRadius);

        // Assert - expect null when no spots are found
        assertNull(result);
    }
}
