package com.plantpal.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GrowthRecordRequest {

    @NotNull(message = "Record date is required")
    @PastOrPresent(message = "Record date cannot be in the future")
    private LocalDate recordDate;

    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Height cannot exceed 9999.99 cm")
    private BigDecimal heightCm;

    @Min(value = 0, message = "Leaf count cannot be negative")
    @Max(value = 100000, message = "Leaf count cannot exceed 100,000")
    private Integer leafCount;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public GrowthRecordRequest() {
    }

    public GrowthRecordRequest(LocalDate recordDate, BigDecimal heightCm, Integer leafCount, String notes) {
        this.recordDate = recordDate;
        this.heightCm = heightCm;
        this.leafCount = leafCount;
        this.notes = notes;
    }

    public boolean hasAtLeastOneField() {
        return heightCm != null || leafCount != null || (notes != null && !notes.trim().isEmpty());
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public Integer getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(Integer leafCount) {
        this.leafCount = leafCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}