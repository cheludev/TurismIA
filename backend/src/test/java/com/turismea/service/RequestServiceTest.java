package com.turismea.service;

import com.turismea.exception.RequestNotFoundException;
import com.turismea.model.entity.Moderator;
import com.turismea.model.entity.Request;
import com.turismea.model.entity.Tourist;
import com.turismea.model.entity.User;
import com.turismea.model.enumerations.Province;
import com.turismea.model.enumerations.RequestStatus;
import com.turismea.model.enumerations.RequestType;
import com.turismea.model.enumerations.Role;
import com.turismea.repository.RequestRepository;
import com.turismea.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateRequest_UserExists() {
        User user = new User();
        user.setUsername("validUser");

        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.createRequest(user, RequestType.TO_MODERATOR, "Valid reasons", Province.HUELVA);

        assertNotNull(result, "The created request should not be null.");
        assertEquals(RequestType.TO_MODERATOR, result.getType(), "The request type should be TO_MODERATOR.");
        verify(requestRepository).save(any(Request.class));
    }


    @Test
    void testCreateRequest_UserNotExist() {
        User user = new User();
        user.setUsername("invalidUser");

        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(false);

        Request result = requestService.createRequest(user, RequestType.TO_MODERATOR, "Valid reasons", Province.HUELVA);

        assertNull(result, "The request should not be created for non-existing users.");
        verify(requestRepository, never()).save(any(Request.class));
    }


    @Test
    void testManageRequest_ApproveRequest() {
        Long requestId = 1L;
        Province province = Province.HUELVA;
        User user = new User();
        user.setRole(Role.TOURIST);

        Request request = new Request(user, RequestType.TO_MODERATOR, "Valid reasons", province);
        request.setId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        requestService.manageRequest(requestId, RequestStatus.APPROVED, province);

        assertEquals(RequestStatus.APPROVED, request.getStatus(), "The request status should be updated to APPROVED.");
        verify(requestRepository).save(request);
        verify(userService).updateUser(user);
    }


    @Test
    void testManageRequest_DenyRequest() {
        Long requestId = 1L;
        Province province = Province.HUELVA;
        User user = new User();

        Request request = new Request(user, RequestType.TO_MODERATOR, "Valid reasons", province);
        request.setId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        requestService.manageRequest(requestId, RequestStatus.DENIED, province);

        assertEquals(RequestStatus.DENIED, request.getStatus(), "The request status should be updated to DENIED.");
        verify(requestRepository).save(request);
        verify(userService).updateUser(user);
    }


    @Test
    void testManageRequest_RequestNotFound() {
        Long requestId = 1L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> requestService.manageRequest(requestId, RequestStatus.APPROVED, Province.HUELVA));
        verify(requestRepository).findById(requestId);
    }


    @Test
    void testApproveModeratorRequest() {
        User user = new User();
        user.setRole(Role.TOURIST);

        User updatedUser = requestService.approveModeratorRequest(user);

        assertEquals(Role.MODERATOR, updatedUser.getRole(), "The user's role should be updated to MODERATOR.");
    }


    @Test
    void testApproveProvinceRequest() {
        Moderator user = new Moderator();

        User updatedUser = requestService.approveProvinceRequest(user, Province.HUELVA);

        assertEquals(Province.HUELVA, updatedUser.getProvince(), "The user's province should be updated.");
    }


    @Test
    void testDenyRequest() {
        User user = new User();
        Request request = new Request(user, RequestType.TO_MODERATOR, "Valid reasons", Province.HUELVA);
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        requestService.denyRequest(request);

        assertEquals(RequestStatus.DENIED, request.getStatus(), "The request should be marked as DENIED.");
    }

    @Test
    void testDeleteRequest_Success() {
        Long requestId = 1L;
        User user = new User();
        Request request = new Request(user, RequestType.TO_MODERATOR, "Valid reasons", Province.HUELVA);
        request.setId(requestId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        requestService.deleteRequest(requestId);

        verify(requestRepository).delete(request);
    }


    @Test
    void testDeleteRequest_NotFound() {
        Long requestId = 1L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> requestService.deleteRequest(requestId));
        verify(requestRepository).findById(requestId);
    }

    @Test
    void testExistsByUserAndType() {
        Tourist tourist = new Tourist();
        RequestType requestType = RequestType.TO_MODERATOR;

        when(requestRepository.existsByUserAndType(tourist, requestType)).thenReturn(true);

        boolean result = requestService.existsByUserAndType(tourist, requestType);

        assertTrue(result, "The method should return true if the request exists.");
    }


    @Test
    void testSaveRequest() {
        Request request = new Request();

        requestService.save(request);

        verify(requestRepository).save(request);
    }
}
