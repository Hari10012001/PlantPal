package com.plantpal.dto.request;

import com.plantpal.enums.PlantStatus;
import jakarta.validation.constraints.NotNull;

public class PlantStatusRequest {

    @NotNull(message = "Plant status is required")
    private PlantStatus status;

    public PlantStatusRequest() {
    }

    public PlantStatusRequest(PlantStatus status) {
        this.status = status;
    }

    public PlantStatus getStatus() {
        return status;
    }

    public void setStatus(PlantStatus status) {
        this.status = status;
    }
}