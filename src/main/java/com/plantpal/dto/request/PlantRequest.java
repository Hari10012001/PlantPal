package com.plantpal.dto.request;

import com.plantpal.enums.PlantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
}