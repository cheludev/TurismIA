package com.turismea.service;

import com.turismea.exception.ReportNotFoundException;
import com.turismea.model.entity.Admin;
import com.turismea.model.entity.Report;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.model.enumerations.ReportAction;
import com.turismea.model.enumerations.ReportType;
import com.turismea.repository.ReportRepository;
import com.turismea.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final RouteService routeService;
    private final TouristService touristService;
    private final AdminService adminService;

    public ReportService(ReportRepository reportRepository, RouteRepository routeService, RouteService routeRepository1,
                         TouristService touristService, AdminService adminService) {
        this.reportRepository = reportRepository;
        this.routeService = routeRepository1;
        this.touristService = touristService;
        this.adminService = adminService;
    }

    @Transactional
    public Report createReport(Tourist tourist, Route route, String description) {
        Admin admin = adminService.findFirstByOrderByIdAsc()
                .orElseGet(() -> adminService.save(new Admin("admin", "administrator081216")));

        Report report = new Report(admin, route, tourist, description);
        return reportRepository.save(report);
    }

    @Transactional
    public void manageReport(Long reportId, ReportAction action) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        if (action == ReportAction.NOTHING) {
            deleteReport(report);
        } else {
            processReport(report);
        }
    }

    private void processReport(Report report) {
        if (report.getType() == ReportType.ROUTE) {
            deleteRoute(report);
        } else if (report.getType() == ReportType.TOURIST) {
            deleteTourist(report);
        }
        deleteReport(report);
    }

    void deleteRoute(Report report) {
        Route route = report.getRoute();
        if (route != null) {
            routeService.delete(route);
        }
    }

    void deleteTourist(Report report) {
        Tourist tourist = report.getTourist();
        if (tourist != null) {
            touristService.delete(tourist);
        }
    }

    public void deleteReport(Report report) {
        reportRepository.delete(report);
    }

    public void save(Report report) {
        reportRepository.save(report);
    }
}
