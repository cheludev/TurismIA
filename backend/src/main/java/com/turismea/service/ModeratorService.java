package com.turismea.service;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.Moderator;
import com.turismea.model.User;
import com.turismea.model.enumerations.Province;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final UserRepository userRepository;

    public ModeratorService(ModeratorRepository moderatorRepository, UserRepository userRepository){
        this.moderatorRepository = moderatorRepository;
        this.userRepository = userRepository;
    }


    public Moderator registerModerator(User user, Province province) {
        Moderator moderator = new Moderator();
        moderator.setUsername(user.getUsername());
        moderator.setPassword(user.getPassword());
        moderator.setEmail(user.getEmail());
        moderator.setProvince(province);
        moderatorRepository.save(moderator);

        return moderator;
    }

    public boolean deleteModerator(Long id) {
        moderatorRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        moderatorRepository.removeModeratorById(id);
        return userRepository.existsUserById(id);
    }

    public void applyToChangeTheProvince(Long moderatorId, String newProvince){

    }


}
