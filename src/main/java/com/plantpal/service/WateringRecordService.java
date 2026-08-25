package com.plantpal.service;

import com.plantpal.dto.request.WateringRecordRequest;
import com.plantpal.dto.response.WateringRecordResponse;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.entity.WateringRecord;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.PlantRepository;
import com.plantpal.repository.WateringRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WateringRecordService {

    private final WateringRecordRepository wateringRecordRepository;
    private final PlantRepository plantRepository;
    private final CareScheduleRepository careScheduleRepository;

    public WateringRecordService(WateringRecordRepository wateringRecordRepository,
                                 PlantRepository plantRepository,
                                 CareScheduleRepository careScheduleRepository) {
        this.wateringRecordRepository = wateringRecordRepository;
        this.plantRepository = plantRepository;
        this.careScheduleRepository = careScheduleRepository;
    }

    public List<WateringRecordResponse> getWateringHistory(Long plantId, Long userId) {
        // Ownership check: must verify plant belongs to current user
        plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        return wateringRecordRepository.findByPlantIdAndPlantUserIdOrderByWateredDateDescCreatedAtDesc(plantId, userId)
                .stream()
                .map(WateringRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public WateringRecordResponse recordWatering(Long plantId, WateringRecordRequest request, Long userId) {
        // Ownership check: must verify plant belongs to current user
        Plant plant = plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        WateringRecord record = new WateringRecord(
                plant,
                request.getWateredDate(),
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        WateringRecord savedRecord = wateringRecordRepository.save(record);

        // Side effect: update care_schedules.last_watered_date if wateredDate >= current last_watered_date (or if null)
        careScheduleRepository.findByPlantId(plantId).ifPresent(careSchedule -> {
            if (careSchedule.getLastWateredDate() == null ||
                    !request.getWateredDate().isBefore(careSchedule.getLastWateredDate())) {
                careSchedule.setLastWateredDate(request.getWateredDate());
                careScheduleRepository.save(careSchedule);
            }
        });

        return WateringRecordResponse.fromEntity(savedRecord);
    }
}