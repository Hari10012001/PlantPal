package com.plantpal.dto.response;

import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.WateringStatus;

import java.time.LocalDate;

public class RecentPlantResponse {

    private Long id;
    private String name;
    private String categoryName;
    private PlantStatus status;
    private WateringStatus wateringStatus;
    private LocalDate lastWateredDate;
    private LocalDate nextWateringDate;

    public RecentPlantResponse() {
    }

    public RecentPlantResponse(Long id, String name, String categoryName, PlantStatus status,
                               WateringStatus wateringStatus, LocalDate lastWateredDate, LocalDate nextWateringDate) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.status = status;
        this.wateringStatus = wateringStatus;
        this.lastWateredDate = lastWateredDate;
        this.nextWateringDate = nextWateringDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public PlantStatus getStatus() {
        return status;
    }

    public void setStatus(PlantStatus status) {
        this.status = status;
    }

    public WateringStatus getWateringStatus() {
        return wateringStatus;
    }

    public void setWateringStatus(WateringStatus wateringStatus) {
        this.wateringStatus = wateringStatus;
    }

    public LocalDate getLastWateredDate() {
        return lastWateredDate;
    }

    public void setLastWateredDate(LocalDate lastWateredDate) {
        this.lastWateredDate = lastWateredDate;
    }

    public LocalDate getNextWateringDate() {
        return nextWateringDate;
    }

    public void setNextWateringDate(LocalDate nextWateringDate) {
        this.nextWateringDate = nextWateringDate;
    }
}