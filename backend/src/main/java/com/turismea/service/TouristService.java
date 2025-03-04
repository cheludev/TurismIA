package com.turismea.service;

import com.turismea.exception.NotTheOwnerOfRouteEception;
import com.turismea.exception.RouteNotFoundException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.*;
import com.turismea.repository.AdminRepository;
import com.turismea.repository.RouteRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private final AdminRepository adminRepository;
    private RouteRepository routeRepository;


    public TouristService(TouristRepository touristRepository, AdminRepository adminRepository) {
        this.touristRepository = touristRepository;
        this.adminRepository = adminRepository;
    }

    public Tourist registerTourist(User user) {
        Tourist tourist = new Tourist();
        tourist.setUsername(user.getUsername());
        tourist.setPassword(user.getPassword());
        tourist.setEmail(user.getEmail());;

        touristRepository.save((tourist));

        return tourist;
    }

    public boolean applyToModerator(Long touristId) {

        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        if(!tourist.getRole().equals(Role.MODERATOR)) {
            Promotion promotion = new Promotion(tourist);
            adminRepository.getReportList(tourist.getId()).add(promotion);
            return adminRepository.getReportList(tourist.getId()).contains(promotion);
        }
        return false;
    }

    public Tourist editTourist(Long touristId, Tourist tourist) {
        Tourist touristToEdit = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        touristToEdit.setUsername(tourist.getUsername());
        touristToEdit.setPassword(tourist.getPassword());
        touristToEdit.setEmail(tourist.getEmail());
        touristToEdit.setRole(tourist.getRole());
        touristToEdit.setPhoto(tourist.getPhoto());
        touristRepository.save(touristToEdit);

        return touristToEdit;
    }



}
