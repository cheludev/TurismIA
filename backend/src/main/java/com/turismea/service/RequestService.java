package com.turismea.service;

import com.turismea.exception.RequestNotFoundException;
import com.turismea.model.Request;
import com.turismea.model.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.repository.RequestRepository;
import com.turismea.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public void manageRequest(Long requestId, RequestStatus requestStatus, Province province) {

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        User user = request.getUser();

        if(request.getType() == RequestType.TO_MODERATOR){
            if(requestStatus == RequestStatus.APPROVED) {
                user.setRole(Role.MODERATOR);
            }
        } else {
            if(requestStatus == RequestStatus.APPROVED) {
                user.setProvince(province);
            }
        }
        request.setStatus(requestStatus);
        requestRepository.save(request);
        userRepository.save(user);
    }

}
