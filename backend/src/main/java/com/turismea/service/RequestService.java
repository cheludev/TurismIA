package com.turismea.service;

import com.turismea.exception.RequestNotFoundException;
import com.turismea.model.*;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.repository.RequestRepository;
import com.turismea.repository.UserRepository;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    RequestService(RequestRepository requestRepository, UserRepository userRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Request createRequest(User user, RequestType type,  String reasonsOfTheRequest, Province province) {
        if(userRepository.existsUserByUsername(user.getUsername())){
            return requestRepository.save(new Request(user, type, reasonsOfTheRequest, province));
        }
        return null;
    }

    public void manageRequest(Long requestId, RequestStatus requestStatus, Province province) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        User user = request.getUser();

        if (requestStatus == RequestStatus.APPROVED) {
            user = approveRequest(request, user, province);
        } else {
            denyRequest(request);
        }

        request.setStatus(requestStatus);
        requestRepository.save(request);
        userService.updateUser(user);
    }

    public User approveRequest(Request request, User user, Province province) {
        if (request.getType() == RequestType.TO_MODERATOR) {
            return approveModeratorRequest(user);
        } else {
            return approveProvinceRequest(user, province);
        }
    }

    public User approveModeratorRequest(User user) {
        user.setRole(Role.MODERATOR);
        return user;
    }

    public User approveProvinceRequest(User user, Province province) {
        user.setProvince(province);
        return user;
    }

    public void denyRequest(Request request) {
        request.setStatus(RequestStatus.DENIED);
    }

    public void deleteRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        requestRepository.delete(request);
    }


    public boolean existsByUserAndType(User user, RequestType requestType) {
        return requestRepository.existsByUserAndType(user, requestType);
    }

    public void save(Request request) {
        requestRepository.save(request);
    }
}
