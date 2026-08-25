package com.plantpal.dto.request;

import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.SunlightNeeds;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PlantRequest {

    @NotBlank(message = "Plant name is required")
    @Size(min = 2, max = 100, message = "Plant name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Size(max = 150, message = "Species cannot exceed 150 characters")
    private String species;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private PlantStatus status;

    // Optional Care Schedule configuration on plant create/update
    @Min(value = 1, message = "Watering interval must be at least 1 day")
    @Max(value = 365, message = "Watering interval cannot exceed 365 days")
    private Integer wateringIntervalDays = 7;

    @PastOrPresent(message = "Last watered date cannot be in the future")
    private LocalDate lastWateredDate;

    private SunlightNeeds sunlightNeeds;

    @Min(value = 1, message = "Fertilizing interval must be at least 1 day")
    private Integer fertilizingIntervalDays;

    public PlantRequest() {
    }

    public PlantRequest(String name, Long categoryId, String species,
                        String description, String location, PlantStatus status) {
        this.name = name;
        this.categoryId = categoryId;
        this.species = species;
        this.description = description;
        this.location = location;
        this.status = status;
        this.wateringIntervalDays = 7;
    }

    public PlantRequest(String name, Long categoryId, String species,
                        String description, String location, PlantStatus status,
                        Integer wateringIntervalDays, LocalDate lastWateredDate,
                        SunlightNeeds sunlightNeeds, Integer fertilizingIntervalDays) {
        this.name = name;
        this.categoryId = categoryId;
        this.species = species;
        this.description = description;
        this.location = location;
        this.status = status;
        this.wateringIntervalDays = (wateringIntervalDays != null) ? wateringIntervalDays : 7;
        this.lastWateredDate = lastWateredDate;
        this.sunlightNeeds = sunlightNeeds;
        this.fertilizingIntervalDays = fertilizingIntervalDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PlantStatus getStatus() {
        return status;
    }

    public void setStatus(PlantStatus status) {
        this.status = status;
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