package com.turismea.service;

import com.turismea.exception.ReportNotFoundException;
import com.turismea.model.Admin;
import com.turismea.model.Report;
import com.turismea.model.Route;
import com.turismea.model.Tourist;
import com.turismea.model.enumerations.ReportAction;
import com.turismea.model.enumerations.ReportType;
import com.turismea.repository.AdminRepository;
import com.turismea.repository.ReportRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final RouteRepository routeRepository;
    private final TouristRepository touristRepository;
    private final AdminRepository adminRepository;

    public ReportService(ReportRepository reportRepository, RouteRepository routeRepository,
                         TouristRepository touristRepository, AdminRepository adminRepository) {
        this.reportRepository = reportRepository;
        this.routeRepository = routeRepository;
        this.touristRepository = touristRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Report createReport(Tourist tourist, Route route, String description) {
        Admin admin = adminRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> adminRepository.save(new Admin()));

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

    private void deleteRoute(Report report) {
        Route route = report.getRoute();
        if (route != null) {
            routeRepository.delete(route);
        }
    }

    private void deleteTourist(Report report) {
        Tourist tourist = report.getTourist();
        if (tourist != null) {
            touristRepository.delete(tourist);
        }
    }

    public void deleteReport(Report report) {
        reportRepository.delete(report);
    }
}
