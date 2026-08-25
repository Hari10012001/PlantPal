package com.plantpal.service;

import com.plantpal.dto.request.GrowthRecordRequest;
import com.plantpal.dto.response.GrowthRecordResponse;
import com.plantpal.entity.GrowthRecord;
import com.plantpal.entity.Plant;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.GrowthRecordRepository;
import com.plantpal.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrowthRecordService {

    private final GrowthRecordRepository growthRecordRepository;
    private final PlantRepository plantRepository;

    public GrowthRecordService(GrowthRecordRepository growthRecordRepository, PlantRepository plantRepository) {
        this.growthRecordRepository = growthRecordRepository;
        this.plantRepository = plantRepository;
    }

    public List<GrowthRecordResponse> getGrowthHistory(Long plantId, Long userId) {
        // Ownership check: must verify plant belongs to current user
        plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        return growthRecordRepository.findByPlantIdAndPlantUserIdOrderByRecordDateDescCreatedAtDesc(plantId, userId)
                .stream()
                .map(GrowthRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public GrowthRecordResponse recordGrowth(Long plantId, GrowthRecordRequest request, Long userId) {
        // Validation: At least one observation field must be provided
        if (!request.hasAtLeastOneField()) {
            throw new IllegalArgumentException("At least one of height, leaf count, or notes must be provided");
        }

        // Ownership check: must verify plant belongs to current user
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        GrowthRecord record = new GrowthRecord(
                plant,
                request.getRecordDate(),
                request.getHeightCm(),
                request.getLeafCount(),
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        GrowthRecord saved = growthRecordRepository.save(record);
        return GrowthRecordResponse.fromEntity(saved);
    }
}