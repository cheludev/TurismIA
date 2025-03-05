package com.turismea.service;

import com.turismea.exception.ReportNotFoundException;
import com.turismea.model.Report;
import com.turismea.model.Route;
import com.turismea.model.Tourist;
import com.turismea.model.enumerations.ReportAction;
import com.turismea.repository.ReportRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final RouteRepository routeRepository;
    private final TouristRepository touristRepository;

    ReportService(ReportRepository reportRepository, RouteRepository routeRepository, TouristRepository touristRepository) {
        this.reportRepository = reportRepository;
        this.routeRepository = routeRepository;
        this.touristRepository = touristRepository;
    }

    public void manageReport(Long reportId, ReportAction action) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));

        switch (report.getType()) {
            case ROUTE -> {
                if(action == ReportAction.NOTHING) {
                    reportRepository.delete(report);
                } else { //Implies we have to delete the route
                    routeRepository.delete(report.getRoute());
                }
            }
            case TOURIST -> {
                if(action == ReportAction.NOTHING) {
                    reportRepository.delete(report);
                } else { //Implies we have to delete the route
                    touristRepository.delete(report.getTourist());
                }
            }
        }

    }

}
