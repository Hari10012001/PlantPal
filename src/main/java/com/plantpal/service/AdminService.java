package com.plantpal.service;

import com.plantpal.dto.response.AdminStatsResponse;
import com.plantpal.dto.response.AdminUserResponse;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.repository.GrowthRecordRepository;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.PlantRepository;
import com.plantpal.repository.UserRepository;
import com.plantpal.repository.WateringRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final WateringRecordRepository wateringRecordRepository;
    private final GrowthRecordRepository growthRecordRepository;
    private final PlantCategoryRepository categoryRepository;

    public AdminService(UserRepository userRepository,
                        PlantRepository plantRepository,
                        WateringRecordRepository wateringRecordRepository,
                        GrowthRecordRepository growthRecordRepository,
                        PlantCategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.wateringRecordRepository = wateringRecordRepository;
        this.growthRecordRepository = growthRecordRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return users.stream().map(user -> {
            long plantCount = plantRepository.countByUserId(user.getId());
            return new AdminUserResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    plantCount,
                    user.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getSystemStats() {
        long totalUsers = userRepository.count();
        long totalPlants = plantRepository.count();
        long totalWateringRecords = wateringRecordRepository.count();
        long totalGrowthRecords = growthRecordRepository.count();
        long totalCategories = categoryRepository.count();

        Map<String, Long> plantsByStatus = new HashMap<>();
        plantsByStatus.put("HEALTHY", plantRepository.countByStatus(PlantStatus.HEALTHY));
        plantsByStatus.put("NEEDS_ATTENTION", plantRepository.countByStatus(PlantStatus.NEEDS_ATTENTION));
        plantsByStatus.put("INACTIVE", plantRepository.countByStatus(PlantStatus.INACTIVE));

        return new AdminStatsResponse(
                totalUsers,
                totalPlants,
                totalWateringRecords,
                totalGrowthRecords,
                totalCategories,
                plantsByStatus
        );
    }
}