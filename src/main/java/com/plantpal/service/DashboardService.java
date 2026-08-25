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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        List<Plant> plants = plantRepository.findByUserIdOrderByCreatedAtDesc(userId);
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(7);

        long waterTodayCount = 0;
        long overdueCount = 0;

        List<RecentPlantResponse> recentPlants = new ArrayList<>();
        List<UpcomingCareResponse> upcomingCare = new ArrayList<>();

        for (Plant plant : plants) {
            CareSchedule schedule = careScheduleRepository.findByPlantId(plant.getId()).orElse(null);

            WateringStatus wateringStatus = WateringStatus.NOT_SET;
            LocalDate nextWateringDate = null;
            LocalDate lastWateredDate = null;

            if (schedule != null) {
                lastWateredDate = schedule.getLastWateredDate();
                if (lastWateredDate != null && schedule.getWateringIntervalDays() != null && schedule.getWateringIntervalDays() > 0) {
                    nextWateringDate = lastWateredDate.plusDays(schedule.getWateringIntervalDays());

                    if (nextWateringDate.isBefore(today)) {
                        wateringStatus = WateringStatus.WATER_OVERDUE;
                        overdueCount++;
                    } else if (nextWateringDate.isEqual(today)) {
                        wateringStatus = WateringStatus.WATER_TODAY;
                        waterTodayCount++;
                    } else {
                        wateringStatus = WateringStatus.WATER_UPCOMING;
                    }

                    // Include in upcomingCare if within 7 days (including overdue and today)
                    if (!nextWateringDate.isAfter(cutoff)) {
                        upcomingCare.add(new UpcomingCareResponse(
                                plant.getId(),
                                plant.getName(),
                                nextWateringDate,
                                wateringStatus
                        ));
                    }
                }
            }

            if (recentPlants.size() < 5) {
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
        }

        // Sort upcoming care chronologically ascending (overdue & today first)
        upcomingCare.sort(Comparator.comparing(UpcomingCareResponse::getNextWateringDate)
                .thenComparing(UpcomingCareResponse::getPlantName));

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