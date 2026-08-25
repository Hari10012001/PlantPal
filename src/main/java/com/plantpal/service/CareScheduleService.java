package com.plantpal.service;

import com.plantpal.dto.request.CareScheduleRequest;
import com.plantpal.dto.response.CareScheduleResponse;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CareScheduleService {

    private final CareScheduleRepository careScheduleRepository;
    private final PlantRepository plantRepository;

    public CareScheduleService(CareScheduleRepository careScheduleRepository, PlantRepository plantRepository) {
        this.careScheduleRepository = careScheduleRepository;
        this.plantRepository = plantRepository;
    }

    public CareScheduleResponse getCareSchedule(Long plantId, Long userId) {
        // Ownership check: must verify plant belongs to user
        plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        CareSchedule schedule = careScheduleRepository.findByPlantId(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Care schedule not found for plant id: " + plantId));

        return CareScheduleResponse.fromEntity(schedule);
    }

    @Transactional
    public CareScheduleResponse updateCareSchedule(Long plantId, CareScheduleRequest request, Long userId) {
        // Ownership check: must verify plant belongs to user
        plantRepository.findByIdAndUserId(plantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found with id: " + plantId));

        CareSchedule schedule = careScheduleRepository.findByPlantId(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Care schedule not found for plant id: " + plantId));

        schedule.setWateringIntervalDays(request.getWateringIntervalDays());
        schedule.setLastWateredDate(request.getLastWateredDate());
        schedule.setSunlightNeeds(request.getSunlightNeeds());
        schedule.setFertilizingIntervalDays(request.getFertilizingIntervalDays());

        CareSchedule updated = careScheduleRepository.save(schedule);
        return CareScheduleResponse.fromEntity(updated);
    }

    @Transactional
    public CareSchedule createDefaultSchedule(Plant plant, Integer interval, java.time.LocalDate lastWatered,
                                              com.plantpal.enums.SunlightNeeds sunlight, Integer fertInterval) {
        CareSchedule schedule = new CareSchedule(
                plant,
                (interval != null && interval >= 1) ? interval : 7,
                lastWatered,
                sunlight,
                fertInterval
        );
        return careScheduleRepository.save(schedule);
    }
}