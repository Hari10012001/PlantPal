package com.plantpal.controller;

import com.plantpal.dto.request.PlantRequest;
import com.plantpal.dto.request.PlantStatusRequest;
import com.plantpal.dto.response.PlantResponse;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.service.AuthService;
import com.plantpal.service.PlantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PlantController exposes full CRUD plant management for authenticated USERs with strict ownership enforcement.
 */
@RestController
@RequestMapping("/api/plants")
@PreAuthorize("hasRole('USER')")
public class PlantController {

    private final PlantService plantService;
    private final AuthService authService;

    public PlantController(PlantService plantService, AuthService authService) {
        this.plantService = plantService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<PlantResponse>> getMyPlants(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PlantStatus status,
            @RequestParam(required = false) String search) {
        User currentUser = authService.getAuthenticatedUser();
        Long filterCategory = (category != null) ? category : categoryId;
        return ResponseEntity.ok(plantService.getUserPlants(currentUser.getId(), filterCategory, status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponse> getPlantById(@PathVariable Long id) {
        User currentUser = authService.getAuthenticatedUser();
        return ResponseEntity.ok(plantService.getPlantById(id, currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<PlantResponse> createPlant(@Valid @RequestBody PlantRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        PlantResponse response = plantService.createPlant(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantResponse> updatePlant(@PathVariable Long id,
                                                     @Valid @RequestBody PlantRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        PlantResponse response = plantService.updatePlant(id, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PlantResponse> updatePlantStatus(@PathVariable Long id,
                                                           @Valid @RequestBody PlantStatusRequest request) {
        User currentUser = authService.getAuthenticatedUser();
        PlantResponse response = plantService.updatePlantStatus(id, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        User currentUser = authService.getAuthenticatedUser();
        plantService.deletePlant(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}