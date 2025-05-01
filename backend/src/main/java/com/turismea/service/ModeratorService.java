package com.turismea.service;

import com.turismea.exception.AlreadyAppliedException;
import com.turismea.exception.MissingProvinceException;
import com.turismea.exception.UserNotFoundException;
import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.Request;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.TouristRepository;
import com.turismea.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final UserService userService;
    private final TouristRepository touristRepository;
    private final RequestService requestService;
    private final PasswordEncoder passwordEncoder;

    public ModeratorService(ModeratorRepository moderatorRepository, UserRepository userRepository, UserService userService, TouristRepository touristRepository, RequestService requestService, PasswordEncoder passwordEncoder){
        this.moderatorRepository = moderatorRepository;
        this.userService = userService;
        this.touristRepository = touristRepository;
        this.requestService = requestService;
        this.passwordEncoder = passwordEncoder;
    }


    public Moderator registerModerator(User user, Province province) {

        if(user == null){
            return null;
        }

        if(province == null || province == Province.NO_PROVINCE){
            throw new MissingProvinceException(province);
        }

        userService.findUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        Moderator moderator = new Moderator();
        moderator.setUsername(user.getUsername());
        moderator.setPassword(passwordEncoder.encode(user.getPassword()));
        moderator.setEmail(user.getEmail());
        moderator.setProvince(province);
        moderator.setRole(Role.MODERATOR);
        moderatorRepository.save(moderator);

        return moderator;
    }

    public boolean deleteModerator(Long id) {
        moderatorRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        moderatorRepository.removeModeratorById(id);
        return userService.existUserById(id);
    }

    public Moderator save(Moderator moderator) {
        if(moderatorRepository.existsByUsername(moderator.getUsername())){
            throw new UserNotFoundException(moderator.getUsername());
        } else {
           return moderatorRepository.save(moderator);
        }
    }

    public boolean applyToChangeTheProvince(Long moderatorId, Province newProvince, String reasons) {
        Moderator moderator = moderatorRepository.findById(moderatorId).orElseThrow(() -> new UserNotFoundException(moderatorId));
        if (newProvince == Province.NO_PROVINCE) {
            throw new MissingProvinceException(newProvince);
        }
        boolean alreadyApplied = requestService.existsByUserAndType(moderator, RequestType.TO_MODERATOR);
        if (alreadyApplied) {
           throw new AlreadyAppliedException(moderatorId, RequestType.CHANGE_PROVINCE);
        }

        Request request = new Request(moderator, RequestType.TO_MODERATOR, reasons, newProvince);
        requestService.save(request);
        return true;
    }


    public Optional<Moderator> findById(Long userId) {
        return moderatorRepository.findById(userId);
    }
}
