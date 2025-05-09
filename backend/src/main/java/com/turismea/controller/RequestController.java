package com.turismea.controller;

import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.RequestDTO.CreateRequestDTO;
import com.turismea.model.dto.RequestDTO.ManageRequestDTO;
import com.turismea.model.dto.RequestDTO.RequestDTO;
import com.turismea.model.entity.Request;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.model.enumerations.RequestType;
import com.turismea.service.RequestService;
import com.turismea.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;

    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> createRequest(@RequestBody CreateRequestDTO requestDTO) {

        User authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(requestDTO.userId)) {
            return ApiResponseUtils.forbidden("You can't create a request for another user");
        }

        User user = userService.getUserById(requestDTO.userId);

        requestService.createRequest(user, requestDTO.type, requestDTO.reasons, requestDTO.province);

        return ApiResponseUtils.success("Request created successfully");
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RequestDTO>>> getAllRequests() {

        List<Request> requests = requestService.getAllRequests();
        List<RequestDTO> requestDTOs = requests.stream()
                .map(RequestDTO::new)
                .toList();

        return ApiResponseUtils.success("List of all requests", requestDTOs);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {

        User authUser = userService.getUserFromAuth();

        Request request = requestService.getRequestById(id);
        if (!authUser.getRole().name().equals("ADMIN") &&
                !request.getUser().getId().equals(authUser.getId())) {
            return ApiResponseUtils.success("You can't delete this request", null);
        }

        requestService.deleteRequest(id);

        return ApiResponseUtils.success("Request deleted successfully");
    }
}
