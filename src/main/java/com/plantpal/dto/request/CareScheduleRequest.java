package com.plantpal.dto.request;

import com.plantpal.enums.SunlightNeeds;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class CareScheduleRequest {

    @NotNull(message = "Watering interval in days is required")
    @Min(value = 1, message = "Watering interval must be at least 1 day")
    @Max(value = 365, message = "Watering interval cannot exceed 365 days")
    private Integer wateringIntervalDays;

    @PastOrPresent(message = "Last watered date cannot be in the future")
    private LocalDate lastWateredDate;

    private SunlightNeeds sunlightNeeds;

    @Min(value = 1, message = "Fertilizing interval must be at least 1 day")
    private Integer fertilizingIntervalDays;

    public CareScheduleRequest() {
    }

    public CareScheduleRequest(Integer wateringIntervalDays, LocalDate lastWateredDate,
                               SunlightNeeds sunlightNeeds, Integer fertilizingIntervalDays) {
        this.wateringIntervalDays = wateringIntervalDays;
        this.lastWateredDate = lastWateredDate;
        this.sunlightNeeds = sunlightNeeds;
        this.fertilizingIntervalDays = fertilizingIntervalDays;
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
}