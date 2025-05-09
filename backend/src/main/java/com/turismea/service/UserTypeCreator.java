package com.turismea.service;

import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.Admin;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.AdminRepository;
import com.turismea.repository.ModeratorRepository;
import com.turismea.repository.TouristRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserTypeCreator {

    private final TouristRepository touristRepository;
    private final ModeratorRepository moderatorRepository;
    private final AdminRepository adminRepository;

    public UserTypeCreator(TouristRepository touristRepository, ModeratorRepository moderatorRepository, AdminRepository adminRepository) {
        this.touristRepository = touristRepository;
        this.moderatorRepository = moderatorRepository;
        this.adminRepository = adminRepository;
    }

    public User createUserType(Role role, User user) {

        return switch (role) {
            case TOURIST -> new Tourist(user);
            case MODERATOR -> new Moderator(user);
            case ADMIN -> new Admin(user);
            default -> null;
        };
    }

    public User saveUserType(User user) {
        return switch (user.getRole()) {
            case TOURIST -> {
                if (!(user instanceof Tourist tourist))
                    throw new IllegalArgumentException("User must be a Tourist");
                yield touristRepository.save(tourist);
            }
            case MODERATOR -> {
                if (!(user instanceof Moderator moderator))
                    throw new IllegalArgumentException("User must be a Moderator");
                yield moderatorRepository.save(moderator);
            }
            case ADMIN -> {
                if (!(user instanceof Admin admin))
                    throw new IllegalArgumentException("User must be an Admin");
                yield adminRepository.save(admin);
            }
            default -> null;
        };
    }

}
