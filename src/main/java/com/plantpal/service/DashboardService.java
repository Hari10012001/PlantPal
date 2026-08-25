package com.plantpal.service;

import com.plantpal.dto.response.DashboardResponse;
import com.plantpal.dto.response.RecentPlantResponse;
import com.plantpal.dto.response.UpcomingCareResponse;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.WateringStatus;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final PlantRepository plantRepository;
    private final CareScheduleRepository careScheduleRepository;

    public DashboardService(PlantRepository plantRepository, CareScheduleRepository careScheduleRepository) {
        this.plantRepository = plantRepository;
        this.careScheduleRepository = careScheduleRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        long totalPlants = plantRepository.countByUserId(userId);
        long healthyPlants = plantRepository.countByUserIdAndStatus(userId, PlantStatus.HEALTHY);
        long needsAttentionPlants = plantRepository.countByUserIdAndStatus(userId, PlantStatus.NEEDS_ATTENTION);
        long inactivePlants = plantRepository.countByUserIdAndStatus(userId, PlantStatus.INACTIVE);

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(7);

        // 1. Bulk-load all care schedules for the user's plants in a single query (eliminates N+1)
        List<CareSchedule> userSchedules = careScheduleRepository.findByUserIdWithPlantAndCategory(userId);
        Map<Long, CareSchedule> scheduleByPlantId = userSchedules.stream()
                .filter(cs -> cs.getPlant() != null)
                .collect(Collectors.toMap(cs -> cs.getPlant().getId(), cs -> cs, (existing, replacement) -> existing));

        long waterTodayCount = 0;
        long overdueCount = 0;
        List<UpcomingCareResponse> upcomingCare = new ArrayList<>();

        for (CareSchedule schedule : userSchedules) {
            LocalDate lastWateredDate = schedule.getLastWateredDate();
            Integer interval = schedule.getWateringIntervalDays();

            if (lastWateredDate != null && interval != null && interval > 0) {
                LocalDate nextWateringDate = lastWateredDate.plusDays(interval);
                WateringStatus status;

                if (nextWateringDate.isBefore(today)) {
                    status = WateringStatus.WATER_OVERDUE;
                    overdueCount++;
                } else if (nextWateringDate.isEqual(today)) {
                    status = WateringStatus.WATER_TODAY;
                    waterTodayCount++;
                } else {
                    status = WateringStatus.WATER_UPCOMING;
                }

                if (!nextWateringDate.isAfter(cutoff)) {
                    upcomingCare.add(new UpcomingCareResponse(
                            schedule.getPlant().getId(),
                            schedule.getPlant().getName(),
                            nextWateringDate,
                            status
                    ));
                }
            }
        }

        // Sort upcoming care chronologically ascending (overdue & today first, then by plant name)
        upcomingCare.sort(Comparator.comparing(UpcomingCareResponse::getNextWateringDate)
                .thenComparing(UpcomingCareResponse::getPlantName));

        // 2. Database-level limit for top 5 recent plants (eliminates loading all plants for just 5)
        List<Plant> top5Plants = plantRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        List<RecentPlantResponse> recentPlants = new ArrayList<>();

        for (Plant plant : top5Plants) {
            CareSchedule schedule = scheduleByPlantId.get(plant.getId());
            WateringStatus wateringStatus = WateringStatus.NOT_SET;
            LocalDate nextWateringDate = null;
            LocalDate lastWateredDate = null;

            if (schedule != null) {
                lastWateredDate = schedule.getLastWateredDate();
                Integer interval = schedule.getWateringIntervalDays();

                if (lastWateredDate != null && interval != null && interval > 0) {
                    nextWateringDate = lastWateredDate.plusDays(interval);
                    if (nextWateringDate.isBefore(today)) {
                        wateringStatus = WateringStatus.WATER_OVERDUE;
                    } else if (nextWateringDate.isEqual(today)) {
                        wateringStatus = WateringStatus.WATER_TODAY;
                    } else {
                        wateringStatus = WateringStatus.WATER_UPCOMING;
                    }
                }
            }

            recentPlants.add(new RecentPlantResponse(
                    plant.getId(),
                    plant.getName(),
                    plant.getCategory() != null ? plant.getCategory().getName() : null,
                    plant.getStatus(),
                    wateringStatus,
                    lastWateredDate,
                    nextWateringDate
            ));
        }

        return new DashboardResponse(
                totalPlants,
                healthyPlants,
                needsAttentionPlants,
                inactivePlants,
                waterTodayCount,
                overdueCount,
                recentPlants,
                upcomingCare
        );
    }
}