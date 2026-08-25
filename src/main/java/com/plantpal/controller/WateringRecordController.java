package com.plantpal.controller;

import com.plantpal.dto.request.WateringRecordRequest;
import com.plantpal.dto.response.WateringRecordResponse;
import com.plantpal.entity.User;
import com.plantpal.service.AuthService;
import com.plantpal.service.WateringRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WateringRecordController exposes Watering Record endpoints (#14 & #15) for authenticated USERs (plant owner only).
 */
@RestController
@RequestMapping("/api/plants/{plantId}/watering")
@PreAuthorize("hasRole('USER')")
public class WateringRecordController {

    private final WateringRecordService wateringRecordService;
    private final AuthService authService;

    public WateringRecordController(WateringRecordService wateringRecordService, AuthService authService) {
        this.wateringRecordService = wateringRecordService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<WateringRecordResponse>> getWateringHistory(@PathVariable Long plantId) {
        User currentUser = authService.getAuthenticatedUser();
        List<WateringRecordResponse> history = wateringRecordService.getWateringHistory(plantId, currentUser.getId());
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<WateringRecordResponse> recordWatering(@PathVariable Long plantId,
                                                                 @Valid @RequestBody WateringRecordRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        WateringRecordResponse response = wateringRecordService.recordWatering(plantId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}