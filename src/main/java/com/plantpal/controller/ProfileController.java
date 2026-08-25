package com.plantpal.controller;

import com.plantpal.dto.request.ChangePasswordRequest;
import com.plantpal.dto.request.UpdateProfileRequest;
import com.plantpal.dto.response.MessageResponse;
import com.plantpal.dto.response.ProfileResponse;
import com.plantpal.entity.User;
import com.plantpal.service.AuthService;
import com.plantpal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * ProfileController exposes Profile endpoints (#19, #20, #21) for authenticated users.
 */
@RestController
@RequestMapping("/api/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserService userService;
    private final AuthService authService;

    public ProfileController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        User currentUser = authService.getAuthenticatedUser();
        ProfileResponse response = userService.getProfile(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        ProfileResponse response = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }
}