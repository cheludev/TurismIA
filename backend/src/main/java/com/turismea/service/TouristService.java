package com.turismea.service;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.*;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;
    private RouteRepository routeRepository;
    private final PasswordEncoder passwordEncoder;

    public TouristService(TouristRepository touristRepository, AdminRepository adminRepository, ReportRepository reportRepository, RequestRepository requestRepository, PasswordEncoder passwordEncoder) {
        this.touristRepository = touristRepository;
        this.adminRepository = adminRepository;
        this.reportRepository = reportRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tourist registerTourist(User user) {
        Tourist tourist = new Tourist();
        tourist.setUsername(user.getUsername());
        tourist.setPassword(passwordEncoder.encode(user.getPassword()));
        tourist.setEmail(user.getEmail());
        tourist.setRole(Role.TOURIST);
        if(touristRepository.existsTouristByUsername(tourist.getUsername())
                || touristRepository.existsTouristByEmail(tourist.getEmail())){
            return null;
        }
        touristRepository.save((tourist));

        return tourist;
    }

    public boolean applyToModerator(Long touristId, Province province, String reasons) {

        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        if (tourist.getRole() == Role.MODERATOR) {
            return false;
        }

        boolean alreadyApplied = requestRepository.existsByUserAndType(tourist, RequestType.TO_MODERATOR);
        if (alreadyApplied) {
            return false;
        }

        Request request = new Request(tourist, RequestType.TO_MODERATOR, reasons, province);
        requestRepository.save(request);
        return true;
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
