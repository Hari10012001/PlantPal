package com.plantpal.dto.response;

import com.plantpal.enums.WateringStatus;

import java.time.LocalDate;

public class UpcomingCareResponse {

    private Long plantId;
    private String plantName;
    private LocalDate nextWateringDate;
    private WateringStatus wateringStatus;

    public UpcomingCareResponse() {
    }

    public UpcomingCareResponse(Long plantId, String plantName, LocalDate nextWateringDate, WateringStatus wateringStatus) {
        this.plantId = plantId;
        this.plantName = plantName;
        this.nextWateringDate = nextWateringDate;
        this.wateringStatus = wateringStatus;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public LocalDate getNextWateringDate() {
        return nextWateringDate;
    }

    public void setNextWateringDate(LocalDate nextWateringDate) {
        this.nextWateringDate = nextWateringDate;
    }

    public WateringStatus getWateringStatus() {
        return wateringStatus;
    }

    public void setWateringStatus(WateringStatus wateringStatus) {
        this.wateringStatus = wateringStatus;
    }
}