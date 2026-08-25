package com.plantpal.dto.response;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {

    private long totalPlants;
    private long healthyPlants;
    private long needsAttentionPlants;
    private long inactivePlants;
    private long waterTodayCount;
    private long overdueCount;
    private List<RecentPlantResponse> recentPlants = new ArrayList<>();
    private List<UpcomingCareResponse> upcomingCare = new ArrayList<>();

    public DashboardResponse() {
    }

    public DashboardResponse(long totalPlants, long healthyPlants, long needsAttentionPlants,
                             long inactivePlants, long waterTodayCount, long overdueCount,
                             List<RecentPlantResponse> recentPlants, List<UpcomingCareResponse> upcomingCare) {
        this.totalPlants = totalPlants;
        this.healthyPlants = healthyPlants;
        this.needsAttentionPlants = needsAttentionPlants;
        this.inactivePlants = inactivePlants;
        this.waterTodayCount = waterTodayCount;
        this.overdueCount = overdueCount;
        this.recentPlants = recentPlants != null ? recentPlants : new ArrayList<>();
        this.upcomingCare = upcomingCare != null ? upcomingCare : new ArrayList<>();
    }

    public long getTotalPlants() {
        return totalPlants;
    }

    public void setTotalPlants(long totalPlants) {
        this.totalPlants = totalPlants;
    }

    public long getHealthyPlants() {
        return healthyPlants;
    }

    public void setHealthyPlants(long healthyPlants) {
        this.healthyPlants = healthyPlants;
    }

    public long getNeedsAttentionPlants() {
        return needsAttentionPlants;
    }

    public void setNeedsAttentionPlants(long needsAttentionPlants) {
        this.needsAttentionPlants = needsAttentionPlants;
    }

    public long getInactivePlants() {
        return inactivePlants;
    }

    public void setInactivePlants(long inactivePlants) {
        this.inactivePlants = inactivePlants;
    }

    public long getWaterTodayCount() {
        return waterTodayCount;
    }

    public void setWaterTodayCount(long waterTodayCount) {
        this.waterTodayCount = waterTodayCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public List<RecentPlantResponse> getRecentPlants() {
        return recentPlants;
    }

    public void setRecentPlants(List<RecentPlantResponse> recentPlants) {
        this.recentPlants = recentPlants;
    }

    public List<UpcomingCareResponse> getUpcomingCare() {
        return upcomingCare;
    }

    public void setUpcomingCare(List<UpcomingCareResponse> upcomingCare) {
        this.upcomingCare = upcomingCare;
    }
}