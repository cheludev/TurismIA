package com.turismea.service;

import com.turismea.model.dto.LocationDTO;
import com.turismea.model.entity.Spot;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class WKTService {
    public static String createWktPointFromSpot(Spot spot){
        return  String.format(Locale.US, "POINT(%f %f)", spot.getLongitude(), spot.getLatitude());
    }
    public static String createWktPointFromLocation(LocationDTO location){
        return  String.format(Locale.US, "POINT(%f %f)", location.getLongitude(), location.getLatitude());
    }
}
