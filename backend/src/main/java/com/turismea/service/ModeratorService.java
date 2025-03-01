package com.turismea.service;

import com.turismea.repository.ModeratorRepository;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;

@Service
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    public ModeratorService(ModeratorRepository moderatorRepository){
        this.moderatorRepository = moderatorRepository;
    }


}
