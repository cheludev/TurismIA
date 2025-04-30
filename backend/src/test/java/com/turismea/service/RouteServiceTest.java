package com.turismea.service;

import com.turismea.exception.NotTheOwnerOfRouteException;
import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.entity.City;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Spot;
import com.turismea.model.entity.Tourist;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private TouristRepository touristRepository;

    @Mock
    private TouristService touristService;

    @Mock
    private CityService cityService;

    @Mock
    private RouteGeneratorService routeGeneratorService;


    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRoute_TouristExists() {
        Long touristId = 1L;
        Route route = new Route();
        route.setName("Test Route");

        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(touristId);

        when(touristService.findById(touristId)).thenReturn(Optional.of(fakeTourist));
        when(routeRepository.getRouteByOwner(fakeTourist)).thenReturn(List.of(route));
        when(routeService.saveRoute(touristId, route)).thenReturn(List.of(route));

        List<Route> savedRoutes = routeService.saveRoute(touristId, route);

        assertEquals(1, savedRoutes.size());
        assertEquals("Test Route", savedRoutes.get(0).getName());

        verify(routeRepository, times(2)).save(route);
    }

    @Test
    void testSaveRoute_TouristNotFound() {
        Long touristId = 1L;
        Route route = new Route();

        when(touristService.findById(touristId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> routeService.saveRoute(touristId, route));

        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testEditRoute_Success() throws Exception {
        Long touristId = 2L;

        Tourist owner = new Tourist();
        owner.setId(touristId);

        Spot spot1 = new Spot();
        spot1.setName("Spot 1");
        spot1.setAverageTime(100);

        Spot spot2 = new Spot();
        spot2.setName("Spot 2");
        spot2.setAverageTime(200);

        Route originalRoute = new Route();
        originalRoute.setId(1L);
        originalRoute.setOwner(owner);
        originalRoute.setSpots(new LinkedList<>(List.of(spot1, spot2)));

        Route updatedRoute = new Route();
        updatedRoute.setName("Updated Route");
        updatedRoute.setOwner(owner);
        updatedRoute.setSpots(new LinkedList<>(List.of(spot1, spot2)));

        when(touristService.findById(touristId)).thenReturn(Optional.of(owner));

        // Simula que routeGeneratorService.createRoute() calcula la duración correctamente
        Route resultRoute = new Route(updatedRoute);
        resultRoute.setDuration(500);
        when(routeGeneratorService.createRoute(updatedRoute)).thenReturn(resultRoute);

        Route result = routeService.editRoute(originalRoute, updatedRoute, touristId);

        assertEquals("Updated Route", result.getName());
        assertEquals(500, result.getDuration());
        assertEquals(2, result.getSpots().size());
        verify(routeGeneratorService).createRoute(updatedRoute);
    }
    

    @Test
    void testEditRoute_UserNotFound() {
        Long touristId = 99L;

        Tourist owner = new Tourist();
        owner.setId(1L);

        Spot spot = new Spot();
        spot.setName("Spot");
        spot.setAverageTime(100);

        Route originalRoute = new Route();
        originalRoute.setOwner(owner);
        originalRoute.setSpots(new LinkedList<>(List.of(spot)));

        Route updatedRoute = new Route();
        updatedRoute.setSpots(new LinkedList<>(List.of(spot)));

        when(touristService.findById(touristId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> routeService.editRoute(originalRoute, updatedRoute, touristId));

        verify(routeGeneratorService, never()).createRoute(any());
    }


    @Test
    void testGetSavedRoutes_TouristExists() {
        Long touristId = 1L;
        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(touristId);

        Route route1 = new Route();
        Route route2 = new Route();

        when(touristService.findById(touristId)).thenReturn(Optional.of(fakeTourist));
        when(routeRepository.getRouteByOwner(fakeTourist)).thenReturn(Arrays.asList(route1, route2));

        List<Route> routes = routeService.getSavedRoutes(touristId);

        assertEquals(2, routes.size());
        verify(routeRepository).getRouteByOwner(fakeTourist);
    }

    @Test
    void testGetSavedRoutes_TouristNotFound() {
        Long touristId = 1L;
        when(touristService.findById(touristId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> routeService.getSavedRoutes(touristId));

        verify(routeRepository, never()).getRouteByOwner(any(Tourist.class));
    }

    @Test
    void testGetRoutesByCity() {
        String cityName = "Test City";
        City fakeCity = new City();
        fakeCity.setName(cityName);

        Route route1 = new Route();
        Route route2 = new Route();

        when(cityService.findByName(cityName)).thenReturn(Optional.of(Optional.of(fakeCity)));
        when(routeRepository.getRoutesByCity(fakeCity)).thenReturn(Arrays.asList(route1, route2));

        List<Route> routes = routeService.getRoutesByCity(cityName);

        assertEquals(2, routes.size());
        verify(routeRepository).getRoutesByCity(fakeCity);
    }

    @Test
    void testGetRoutesByOwner() {
        Long ownerId = 1L;
        Route route1 = new Route();
        Route route2 = new Route();

        when(routeRepository.findRouteByOwner_Id(ownerId)).thenReturn(Arrays.asList(route1, route2));

        List<Route> routes = routeService.getRoutesByOwner(ownerId);

        assertEquals(2, routes.size());
        verify(routeRepository).findRouteByOwner_Id(ownerId);
    }

    @Test
    void testDeleteRoute() {
        Long routeId = 1L;

        routeService.deleteRoute(routeId);

        verify(routeRepository).deleteById(routeId);
    }

    @Test
    void testGetRouteById_Found() {
        Long routeId = 1L;
        Route route = new Route();
        route.setId(routeId);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        Route result = routeService.getRouteById(routeId);

        assertEquals(routeId, result.getId());
        verify(routeRepository).findById(routeId);
    }

    @Test
    void testGetRouteById_NotFound() {
        Long routeId = 1L;
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.getRouteById(routeId));

        verify(routeRepository).findById(routeId);
    }

    @Test
    void testGetAllRoutes() {
        Route route1 = new Route();
        Route route2 = new Route();

        when(routeRepository.findAll()).thenReturn(Arrays.asList(route1, route2));

        List<Route> routes = routeService.getAllRoutes();

        assertEquals(2, routes.size());
        verify(routeRepository).findAll();
    }
}
