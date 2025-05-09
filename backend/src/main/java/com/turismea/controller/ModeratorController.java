package com.turismea.controller;

import com.turismea.exception.UserNotFoundException;
import com.turismea.model.api_response.ApiResponse;
import com.turismea.model.api_response.ApiResponseUtils;
import com.turismea.model.dto.ModeratorDTO.ModeratorDTO;
import com.turismea.model.dto.UserDTO;
import com.turismea.model.entity.Moderator;
import com.turismea.model.enumerations.Province;
import com.turismea.service.ModeratorService;
import com.turismea.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderators")
public class ModeratorController {

    private final ModeratorService moderatorService;
    private final UserService userService;

    public ModeratorController(ModeratorService moderatorService, UserService userService) {
        this.moderatorService = moderatorService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ModeratorDTO>> getMyModeratorInfo(@PathVariable Long id) {

        var authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't access another user's data", null);
        }

        Moderator moderator = moderatorService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return ApiResponseUtils.success("Moderator info retrieved successfully", new ModeratorDTO (moderator));
    }

    @PostMapping("/{id}/change-province")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> requestProvinceChange(
            @PathVariable Long id,
            @RequestParam Province newProvince,
            @RequestParam String reasons) {

        var authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't request changes for another user", null);
        }

        boolean applied = moderatorService.applyToChangeTheProvince(id, newProvince, reasons);

        if (applied) {
            return ApiResponseUtils.success("Province change request submitted successfully");
        } else {
            return ApiResponseUtils.conflict("Request could not be submitted");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteMyModeratorAccount(@PathVariable Long id) {

        var authUser = userService.getUserFromAuth();

        if (!authUser.getId().equals(id)) {
            return ApiResponseUtils.success("You can't delete another user's moderator account", null);
        }

        boolean deleted = moderatorService.deleteModerator(id);

        if (deleted) {
            return ApiResponseUtils.success("Moderator account deleted successfully");
        } else {
            return ApiResponseUtils.badRequest("Could not delete moderator account");
        }
    }
}
