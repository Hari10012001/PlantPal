package com.plantpal.controller;

import com.plantpal.dto.request.CareScheduleRequest;
import com.plantpal.dto.response.CareScheduleResponse;
import com.plantpal.entity.User;
import com.plantpal.service.AuthService;
import com.plantpal.service.CareScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * CareScheduleController exposes Care Schedule endpoints (#12 & #13) for authenticated USERs (plant owner only).
 */
@RestController
@RequestMapping("/api/plants/{plantId}/care")
@PreAuthorize("hasRole('USER')")
public class CareScheduleController {

    private final CareScheduleService careScheduleService;
    private final AuthService authService;

    public CareScheduleController(CareScheduleService careScheduleService, AuthService authService) {
        this.careScheduleService = careScheduleService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<CareScheduleResponse> getCareSchedule(@PathVariable Long plantId) {
        User currentUser = authService.getAuthenticatedUser();
        CareScheduleResponse response = careScheduleService.getCareSchedule(plantId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CareScheduleResponse> updateCareSchedule(@PathVariable Long plantId,
                                                                   @Valid @RequestBody CareScheduleRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        CareScheduleResponse response = careScheduleService.updateCareSchedule(plantId, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }
}