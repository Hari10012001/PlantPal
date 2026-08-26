package com.plantpal.dto.response;

import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.enums.PlantStatus;

import java.time.LocalDateTime;

public class PlantResponse {

    private Long id;
    private String name;
    private String species;
    private String description;
    private String location;
    private PlantStatus status;
    private CategoryResponse category;
    private CareScheduleResponse careSchedule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlantResponse() {
    }

    public PlantResponse(Long id, String name, String species, String description,
                         String location, PlantStatus status, CategoryResponse category,
                         CareScheduleResponse careSchedule, LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.description = description;
        this.location = location;
        this.status = status;
        this.category = category;
        this.careSchedule = careSchedule;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PlantResponse fromEntity(Plant plant) {
        return fromEntity(plant, null);
    }

    public static PlantResponse fromEntity(Plant plant, CareSchedule careSchedule) {
        if (plant == null) return null;
        return new PlantResponse(
                plant.getId(),
                plant.getName(),
                plant.getSpecies(),
                plant.getDescription(),
                plant.getLocation(),
                plant.getStatus(),
                CategoryResponse.fromEntity(plant.getCategory()),
                CareScheduleResponse.fromEntity(careSchedule),
                plant.getCreatedAt(),
                plant.getUpdatedAt()
        );
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

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public CareScheduleResponse getCareSchedule() {
        return careSchedule;
    }

    public void setCareSchedule(CareScheduleResponse careSchedule) {
        this.careSchedule = careSchedule;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public com.plantpal.enums.WateringStatus getWateringStatus() {
        return careSchedule != null ? careSchedule.getWateringStatus() : null;
    }

    public java.time.LocalDate getNextWateringDate() {
        return careSchedule != null ? careSchedule.getNextWateringDate() : null;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}