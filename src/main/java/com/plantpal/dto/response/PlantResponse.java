package com.plantpal.dto.response;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlantResponse() {
    }

    public PlantResponse(Long id, String name, String species, String description,
                         String location, PlantStatus status, CategoryResponse category,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.description = description;
        this.location = location;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PlantResponse fromEntity(Plant plant) {
        if (plant == null) return null;
        return new PlantResponse(
                plant.getId(),
                plant.getName(),
                plant.getSpecies(),
                plant.getDescription(),
                plant.getLocation(),
                plant.getStatus(),
                CategoryResponse.fromEntity(plant.getCategory()),
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