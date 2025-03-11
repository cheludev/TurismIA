package com.turismea.service;

import com.turismea.exception.ReportNotFoundException;
import com.turismea.model.Admin;
import com.turismea.model.Report;
import com.turismea.model.Route;
import com.turismea.model.Tourist;
import com.turismea.model.enumerations.ReportAction;
import com.turismea.model.enumerations.ReportType;
import com.turismea.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private RouteService routeService;

    @Mock
    private TouristService touristService;

    @InjectMocks
    private ReportService reportService;

    @Mock
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReport_adminExist(){
        Admin admin = new Admin("user", "password");
        Route route = new Route();
        Tourist tourist = new Tourist();
        String description = "desc";

        when(reportRepository.save(any(Report.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(adminService.findFirstByOrderByIdAsc()).thenReturn(Optional.of(admin));
        Report answeredReport = reportService.createReport(tourist, route, description);

        assertNotNull(answeredReport, "The report should not be null.");
        assertEquals(route, answeredReport.getRoute(), "The report's route should match.");
        assertEquals(tourist, answeredReport.getTourist(), "The report's tourist should match.");
        assertEquals(description, answeredReport.getDescription(), "The report's description should match.");
        assertEquals(admin, answeredReport.getAdmin(), "The assigned admin should match.");

        verify(reportRepository).save(any(Report.class));
        verify(adminService).findFirstByOrderByIdAsc();
    }

    @Test
    void testCreateReport_adminNotExist() {
        Admin admin = new Admin("user", "password");
        Route route = new Route();
        Tourist tourist = new Tourist();
        String description = "desc";

        when(reportRepository.save(any(Report.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(adminService.findFirstByOrderByIdAsc()).thenReturn(Optional.of(admin));
        when(adminService.save(any(Admin.class))).thenReturn(admin);

        Report answeredReport = reportService.createReport(tourist, route, description);

        assertNotNull(answeredReport, "The report should not be null.");
        assertEquals(route, answeredReport.getRoute(), "The report's route should match.");
        assertEquals(tourist, answeredReport.getTourist(), "The report's tourist should match.");
        assertEquals(description, answeredReport.getDescription(), "The report's description should match.");
        assertEquals(admin, answeredReport.getAdmin(), "The assigned admin should match.");

        verify(reportRepository).save(any(Report.class));
        verify(adminService).findFirstByOrderByIdAsc();

    }


    @Test
    void testManageReport_DeleteReport_WhenActionIsNothing() {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.manageReport(reportId, ReportAction.NOTHING);

        verify(reportRepository).delete(report);
    }

    @Test
    void testManageReport_ProcessReport_WhenActionIsValid() {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);
        report.setType(ReportType.ROUTE);

        Route route = new Route();
        report.setRoute(route);  // Asegura que el reporte tiene una ruta asociada

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.manageReport(reportId, ReportAction.DELETE);

        verify(routeService, times(1)).delete(route); // Verifica que se elimine la ruta
        verify(reportRepository).delete(report); // Verifica que el reporte se elimine
    }

    @Test
    void testManageReport_ReportNotFound() {
        Long reportId = 1L;

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportService.manageReport(reportId, ReportAction.DELETE));

        verify(reportRepository).findById(reportId);
    }

    @Test
    void testDeleteRoute_WhenReportHasRoute() {
        Report report = new Report();
        Route route = new Route();
        report.setRoute(route);

        reportService.deleteRoute(report);

        verify(routeService).delete(route);
    }

    @Test
    void testDeleteRoute_WhenReportHasNoRoute() {
        Report report = new Report();

        reportService.deleteRoute(report);

        verify(routeService, never()).delete(any(Route.class));
    }

    @Test
    void testDeleteTourist_WhenReportHasTourist() {
        Report report = new Report();
        Tourist tourist = new Tourist();
        report.setTourist(tourist);

        reportService.deleteTourist(report);

        verify(touristService).delete(tourist);
    }

    @Test
    void testDeleteTourist_WhenReportHasNoTourist() {
        Report report = new Report();

        reportService.deleteTourist(report);

        verify(touristService, never()).delete(any(Tourist.class));
    }

    @Test
    void testDeleteReport() {
        Report report = new Report();

        reportService.deleteReport(report);

        verify(reportRepository).delete(report);
    }


    }
