package com.plantpal.dto.response;

import com.plantpal.entity.CareSchedule;
import com.plantpal.enums.SunlightNeeds;
import com.plantpal.enums.WateringStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CareScheduleResponse {

    private Long id;
    private Long plantId;
    private Integer wateringIntervalDays;
    private LocalDate lastWateredDate;
    private LocalDate nextWateringDate;
    private WateringStatus wateringStatus;
    private SunlightNeeds sunlightNeeds;
    private Integer fertilizingIntervalDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CareScheduleResponse() {
    }

    public CareScheduleResponse(Long id, Long plantId, Integer wateringIntervalDays,
                                LocalDate lastWateredDate, LocalDate nextWateringDate,
                                WateringStatus wateringStatus, SunlightNeeds sunlightNeeds,
                                Integer fertilizingIntervalDays, LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        this.id = id;
        this.plantId = plantId;
        this.wateringIntervalDays = wateringIntervalDays;
        this.lastWateredDate = lastWateredDate;
        this.nextWateringDate = nextWateringDate;
        this.wateringStatus = wateringStatus;
        this.sunlightNeeds = sunlightNeeds;
        this.fertilizingIntervalDays = fertilizingIntervalDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CareScheduleResponse fromEntity(CareSchedule schedule) {
        if (schedule == null) return null;
        return new CareScheduleResponse(
                schedule.getId(),
                schedule.getPlant() != null ? schedule.getPlant().getId() : null,
                schedule.getWateringIntervalDays(),
                schedule.getLastWateredDate(),
                schedule.getNextWateringDate(),
                schedule.getWateringStatus(),
                schedule.getSunlightNeeds(),
                schedule.getFertilizingIntervalDays(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public Integer getWateringIntervalDays() {
        return wateringIntervalDays;
    }

    public void setWateringIntervalDays(Integer wateringIntervalDays) {
        this.wateringIntervalDays = wateringIntervalDays;
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

    public WateringStatus getWateringStatus() {
        return wateringStatus;
    }

    public void setWateringStatus(WateringStatus wateringStatus) {
        this.wateringStatus = wateringStatus;
    }

    public SunlightNeeds getSunlightNeeds() {
        return sunlightNeeds;
    }

    public void setSunlightNeeds(SunlightNeeds sunlightNeeds) {
        this.sunlightNeeds = sunlightNeeds;
    }

    public Integer getFertilizingIntervalDays() {
        return fertilizingIntervalDays;
    }

    public void setFertilizingIntervalDays(Integer fertilizingIntervalDays) {
        this.fertilizingIntervalDays = fertilizingIntervalDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}