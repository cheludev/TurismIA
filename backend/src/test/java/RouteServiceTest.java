import com.turismea.exception.NotTheOwnerOfRouteException;
import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.City;
import com.turismea.model.Route;
import com.turismea.model.Tourist;
import com.turismea.repository.CityRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private TouristRepository touristRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private RouteService routeService;

    @Test
    void testSaveRoute_TouristExists() {
        Long touristId = 1L;
        Route route = new Route();
        route.setName("Test Route");

        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(touristId);

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(fakeTourist));
        when(routeRepository.getRouteByOwner(fakeTourist)).thenReturn(List.of(route));

        List<Route> savedRoutes = routeService.saveRoute(touristId, route);

        assertEquals(1, savedRoutes.size());
        assertEquals("Test Route", savedRoutes.get(0).getName());

        verify(routeRepository).save(route);
        verify(touristRepository).save(fakeTourist);
    }

    @Test
    void testSaveRoute_TouristNotFound() {
        Long touristId = 1L;
        Route route = new Route();

        when(touristRepository.findById(touristId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> routeService.saveRoute(touristId, route));

        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testEditRoute_Success() {
        Long routeId = 1L;
        Long touristId = 2L;
        Route existingRoute = new Route();
        existingRoute.setId(routeId);
        Tourist owner = new Tourist();
        owner.setId(touristId);
        existingRoute.setOwner(owner);

        Route updatedRoute = new Route();
        updatedRoute.setName("Updated Route");

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(existingRoute);

        Route result = routeService.editRoute(routeId, updatedRoute, touristId);

        assertEquals("Updated Route", result.getName());
        verify(routeRepository).save(existingRoute);
    }

    @Test
    void testEditRoute_NotTheOwner() {
        Long routeId = 1L;
        Long wrongTouristId = 99L;
        Route existingRoute = new Route();
        existingRoute.setId(routeId);
        Tourist owner = new Tourist();
        owner.setId(2L);
        existingRoute.setOwner(owner);

        Route updatedRoute = new Route();
        updatedRoute.setName("Updated Route");

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));

        assertThrows(NotTheOwnerOfRouteException.class, () -> routeService.editRoute(routeId, updatedRoute, wrongTouristId));

        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testEditRoute_NotFound() {
        Long routeId = 1L;
        Long touristId = 2L;
        Route updatedRoute = new Route();

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.editRoute(routeId, updatedRoute, touristId));

        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testGetSavedRoutes_TouristExists() {
        Long touristId = 1L;
        Tourist fakeTourist = new Tourist();
        fakeTourist.setId(touristId);

        Route route1 = new Route();
        Route route2 = new Route();

        when(touristRepository.findById(touristId)).thenReturn(Optional.of(fakeTourist));
        when(routeRepository.getRouteByOwner(fakeTourist)).thenReturn(Arrays.asList(route1, route2));

        List<Route> routes = routeService.getSavedRoutes(touristId);

        assertEquals(2, routes.size());
        verify(routeRepository).getRouteByOwner(fakeTourist);
    }

    @Test
    void testGetSavedRoutes_TouristNotFound() {
        Long touristId = 1L;
        when(touristRepository.findById(touristId)).thenReturn(Optional.empty());

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

        when(cityRepository.findByName(cityName)).thenReturn(fakeCity);
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
