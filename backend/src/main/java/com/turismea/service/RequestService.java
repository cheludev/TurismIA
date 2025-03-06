package com.turismea.service;

import com.turismea.exception.RequestNotFoundException;
import com.turismea.model.*;
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

    public Request createRequest(User user, RequestType type,  String reasonsOfTheRequest, Province province) {
        return requestRepository.save(new Request(user, type, reasonsOfTheRequest, province));
    }
    public void manageRequest(Long requestId, RequestStatus requestStatus, Province province) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        User user = request.getUser();

        if (requestStatus == RequestStatus.APPROVED) {
            approveRequest(request, user, province);
        }

        request.setStatus(requestStatus);
        //In Java we can modify the parameters of an object in another function
        //It is similar to referenced parameter in C++, so we can save only in the
        //original method
        requestRepository.save(request);
        userRepository.save(user);
    }

    private void approveRequest(Request request, User user, Province province) {
        if (request.getType() == RequestType.TO_MODERATOR) {
            approveModeratorRequest(user);
        } else {
            approveProvinceRequest(user, province);
        }
    }

    private void approveModeratorRequest(User user) {
        user.setRole(Role.MODERATOR);
    }

    private void approveProvinceRequest(User user, Province province) {
        user.setProvince(province);
    }

    private void denyRequest(Request request) {
        request.setStatus(RequestStatus.DENIED);
    }

    public void deleteRequest(Request request) {
        requestRepository.delete(request);
    }


}
