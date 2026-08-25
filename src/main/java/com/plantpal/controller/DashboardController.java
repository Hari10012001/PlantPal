package com.plantpal.controller;

import com.plantpal.dto.response.DashboardResponse;
import com.plantpal.entity.User;
import com.plantpal.service.AuthService;
import com.plantpal.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DashboardController exposes Dashboard endpoint (#18) for authenticated USERs.
 */
@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('USER')")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        User currentUser = authService.getAuthenticatedUser();
        DashboardResponse dashboard = dashboardService.getDashboard(currentUser.getId());
        return ResponseEntity.ok(dashboard);
    }
}