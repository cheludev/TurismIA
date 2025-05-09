package com.turismea.model.dto.ReportDTO;


import com.turismea.model.enumerations.ReportType;

public class CreateReportRequest {
    public Long routeId;
    public String description;
    public ReportType type;  // Puede ser null si no lo envía
}
