package com.turismea.controller;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.ReportDTO.CreateReportRequest;
import com.turismea.model.entity.*;
import com.turismea.model.enumerations.ReportAction;
import com.turismea.model.enumerations.ReportType;
import com.turismea.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final TouristService touristService;
    private final RouteService routeService;
    private final UserService userService;
    private final AdminService adminService;

    public ReportController(ReportService reportService, TouristService touristService,
                            RouteService routeService, UserService userService, AdminService adminService) {
        this.reportService = reportService;
        this.touristService = touristService;
        this.routeService = routeService;
        this.userService = userService;
        this.adminService = adminService;
    }

    public static class CreateReportRequest {
        public Long routeId;
        public String description;
        public ReportType type;  // Opcional
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('TOURIST')")
    public ResponseEntity<ApiResponse<Void>> createReport(
            @RequestBody CreateReportRequest request) {

        User authUser = userService.getUserFromAuth();

        if (!(authUser instanceof Tourist)) {
            return ApiResponseUtils.unauthorized("Only tourists can create reports");
        }

        Tourist tourist = touristService.getTouristById(authUser.getId());
        Route route = routeService.getRouteWithSpotsById(request.routeId);

        Admin admin = adminService.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("No admin found in the system"));

        Report report = new Report(admin, route, tourist, request.description);

        if (request.type != null) {
            report.setType(request.type);
        }

        reportService.save(report);

        return ApiResponseUtils.success("Report created successfully");
    }


    @PutMapping("/{id}/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> manageReport(
            @PathVariable Long id,
            @RequestParam ReportAction action) {

        reportService.manageReport(id, action);

        return ApiResponseUtils.success("Report managed successfully");
    }
}
