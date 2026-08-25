package com.plantpal.controller;

import com.plantpal.dto.request.GrowthRecordRequest;
import com.plantpal.dto.response.GrowthRecordResponse;
import com.plantpal.entity.User;
import com.plantpal.service.AuthService;
import com.plantpal.service.GrowthRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GrowthRecordController exposes Growth Record endpoints (#16 & #17) for authenticated USERs (plant owner only).
 */
@RestController
@RequestMapping("/api/plants/{plantId}/growth")
@PreAuthorize("hasRole('USER')")
public class GrowthRecordController {

    private final GrowthRecordService growthRecordService;
    private final AuthService authService;

    public GrowthRecordController(GrowthRecordService growthRecordService, AuthService authService) {
        this.growthRecordService = growthRecordService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<GrowthRecordResponse>> getGrowthHistory(@PathVariable Long plantId) {
        User currentUser = authService.getAuthenticatedUser();
        List<GrowthRecordResponse> history = growthRecordService.getGrowthHistory(plantId, currentUser.getId());
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<GrowthRecordResponse> recordGrowth(@PathVariable Long plantId,
                                                             @Valid @RequestBody GrowthRecordRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        GrowthRecordResponse response = growthRecordService.recordGrowth(plantId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}