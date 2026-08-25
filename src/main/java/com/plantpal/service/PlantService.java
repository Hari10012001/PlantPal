package com.plantpal.service;

import com.plantpal.dto.request.PlantRequest;
import com.plantpal.dto.request.PlantStatusRequest;
import com.plantpal.dto.response.PlantResponse;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantCategoryRepository categoryRepository;
    private final CareScheduleRepository careScheduleRepository;

    public PlantService(PlantRepository plantRepository,
                        PlantCategoryRepository categoryRepository,
                        CareScheduleRepository careScheduleRepository) {
        this.plantRepository = plantRepository;
        this.categoryRepository = categoryRepository;
        this.careScheduleRepository = careScheduleRepository;
    }

    public List<PlantResponse> getUserPlants(Long userId, Long categoryId, PlantStatus status, String search) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return plantRepository.searchUserPlants(userId, categoryId, status, cleanSearch).stream()
                .map(plant -> {
                    CareSchedule schedule = careScheduleRepository.findByPlantId(plant.getId()).orElse(null);
                    return PlantResponse.fromEntity(plant, schedule);
                })
                .collect(Collectors.toList());
    }

    public PlantResponse getPlantById(Long plantId, Long userId) {
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));
        CareSchedule schedule = careScheduleRepository.findByPlantId(plant.getId()).orElse(null);
        return PlantResponse.fromEntity(plant, schedule);
    }

    @Transactional
    public PlantResponse createPlant(PlantRequest request, User user) {
        PlantCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Plant plant = new Plant(
                user,
                category,
                request.getName().trim(),
                request.getSpecies() != null ? request.getSpecies().trim() : null,
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getLocation() != null ? request.getLocation().trim() : null,
                request.getStatus() != null ? request.getStatus() : PlantStatus.HEALTHY
        );

        Plant savedPlant = plantRepository.save(plant);

        // Auto-create care schedule for the plant
        CareSchedule careSchedule = new CareSchedule(
                savedPlant,
                (request.getWateringIntervalDays() != null && request.getWateringIntervalDays() >= 1) ? request.getWateringIntervalDays() : 7,
                request.getLastWateredDate(),
                request.getSunlightNeeds(),
                request.getFertilizingIntervalDays()
        );
        CareSchedule savedSchedule = careScheduleRepository.save(careSchedule);

        return PlantResponse.fromEntity(savedPlant, savedSchedule);
    }

    @Transactional
    public PlantResponse updatePlant(Long plantId, PlantRequest request, Long userId) {
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        PlantCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        plant.setName(request.getName().trim());
        plant.setCategory(category);
        plant.setSpecies(request.getSpecies() != null ? request.getSpecies().trim() : null);
        plant.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        plant.setLocation(request.getLocation() != null ? request.getLocation().trim() : null);
        if (request.getStatus() != null) {
            plant.setStatus(request.getStatus());
        }

        Plant updatedPlant = plantRepository.save(plant);
        CareSchedule schedule = careScheduleRepository.findByPlantId(plant.getId()).orElse(null);
        return PlantResponse.fromEntity(updatedPlant, schedule);
    }

    @Transactional
    public PlantResponse updatePlantStatus(Long plantId, PlantStatusRequest request, Long userId) {
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        plant.setStatus(request.getStatus());
        Plant updatedPlant = plantRepository.save(plant);
        CareSchedule schedule = careScheduleRepository.findByPlantId(plant.getId()).orElse(null);
        return PlantResponse.fromEntity(updatedPlant, schedule);
    }

    @Transactional
    public void deletePlant(Long plantId, Long userId) {
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        plantRepository.delete(plant);
    }
}