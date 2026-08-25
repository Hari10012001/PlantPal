package com.plantpal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class WateringRecordRequest {

    @NotNull(message = "Watered date is required")
    @PastOrPresent(message = "Watered date cannot be in the future")
    private LocalDate wateredDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public WateringRecordRequest() {
    }

    public WateringRecordRequest(LocalDate wateredDate, String notes) {
        this.wateredDate = wateredDate;
        this.notes = notes;
    }

    public LocalDate getWateredDate() {
        return wateredDate;
    }

    public void setWateredDate(LocalDate wateredDate) {
        this.wateredDate = wateredDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}