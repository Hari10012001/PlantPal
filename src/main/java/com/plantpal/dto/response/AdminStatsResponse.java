package com.plantpal.dto.response;

import java.util.HashMap;
import java.util.Map;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalPlants;
    private long totalWateringRecords;
    private long totalGrowthRecords;
    private long totalCategories;
    private Map<String, Long> plantsByStatus = new HashMap<>();

    public AdminStatsResponse() {
    }

    public AdminStatsResponse(long totalUsers, long totalPlants, long totalWateringRecords,
                              long totalGrowthRecords, long totalCategories, Map<String, Long> plantsByStatus) {
        this.totalUsers = totalUsers;
        this.totalPlants = totalPlants;
        this.totalWateringRecords = totalWateringRecords;
        this.totalGrowthRecords = totalGrowthRecords;
        this.totalCategories = totalCategories;
        this.plantsByStatus = plantsByStatus != null ? plantsByStatus : new HashMap<>();
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPlants() {
        return totalPlants;
    }

    public void setTotalPlants(long totalPlants) {
        this.totalPlants = totalPlants;
    }

    public long getTotalWateringRecords() {
        return totalWateringRecords;
    }

    public void setTotalWateringRecords(long totalWateringRecords) {
        this.totalWateringRecords = totalWateringRecords;
    }

    public long getTotalGrowthRecords() {
        return totalGrowthRecords;
    }

    public void setTotalGrowthRecords(long totalGrowthRecords) {
        this.totalGrowthRecords = totalGrowthRecords;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public Map<String, Long> getPlantsByStatus() {
        return plantsByStatus;
    }

    public void setPlantsByStatus(Map<String, Long> plantsByStatus) {
        this.plantsByStatus = plantsByStatus;
    }
}