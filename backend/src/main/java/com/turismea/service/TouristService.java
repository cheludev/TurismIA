package com.turismea.service;

import com.turismea.exception.AlreadyAppliedException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.entity.Request;
import com.turismea.model.entity.Route;
import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TouristService {

    private final TouristRepository touristRepository;
    private final RequestService requestService;
    private final PasswordEncoder passwordEncoder;
    private final RouteRepository routeRepository;

    public TouristService(TouristRepository touristRepository, RequestService requestService,
                          PasswordEncoder passwordEncoder, RouteRepository routeRepository) {
        this.touristRepository = touristRepository;
        this.requestService = requestService;
        this.passwordEncoder = passwordEncoder;
        this.routeRepository = routeRepository;
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
        return touristRepository.save((tourist));
    }

    public boolean applyToModerator(Long touristId, Province province, String reasons) {

        Tourist tourist = touristRepository.findById(touristId)
                .orElseThrow(() -> new UserNotFoundException(touristId));

        if (tourist.getRole() == Role.MODERATOR) {
            return false;
        }

        boolean alreadyApplied = requestService.existsByUserAndType(tourist, RequestType.TO_MODERATOR);
        if (alreadyApplied) {
            throw new AlreadyAppliedException(touristId, RequestType.TO_MODERATOR);
        }

        Request request = new Request(tourist, RequestType.TO_MODERATOR, reasons, province);
        requestService.save(request);
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
        return touristRepository.save(touristToEdit);
    }

    public void delete(Tourist tourist) {
        touristRepository.delete(tourist);
    }

    public Tourist save(Tourist tourist) {
        return touristRepository.save(tourist);
    }

    public Tourist existByUsername(String username) {
        if(username.isEmpty()) {
        }
        if(touristRepository.findByUsername(username).isPresent()){
            return touristRepository.findByUsername(username).get();
        } else {
            throw new UserNotFoundException(username);
        }
    }

    public Optional<Tourist> findById(Long touristId) {
        return touristRepository.findById(touristId);
    }



    public Tourist getTouristById(Long id) {
        return touristRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

}
