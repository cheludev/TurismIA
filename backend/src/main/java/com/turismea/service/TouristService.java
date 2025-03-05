package com.turismea.service;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.*;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.*;
import org.springframework.stereotype.Service;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;
    private RouteRepository routeRepository;


    public TouristService(TouristRepository touristRepository, AdminRepository adminRepository, ReportRepository reportRepository, RequestRepository requestRepository) {
        this.touristRepository = touristRepository;
        this.adminRepository = adminRepository;
        this.reportRepository = reportRepository;
        this.requestRepository = requestRepository;
    }

    public Tourist registerTourist(User user) {
        Tourist tourist = new Tourist();
        tourist.setUsername(user.getUsername());
        tourist.setPassword(user.getPassword());
        tourist.setEmail(user.getEmail());;

        touristRepository.save((tourist));

        return tourist;
    }

    public boolean applyToModerator(Long touristId, String province) {

        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        if(!tourist.getRole().equals(Role.MODERATOR)) {
            Request promotion = new Request(tourist); //It is already added to tourist

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
