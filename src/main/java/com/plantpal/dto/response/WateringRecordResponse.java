package com.plantpal.dto.response;

import com.plantpal.entity.WateringRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WateringRecordResponse {

    private Long id;
    private Long plantId;
    private LocalDate wateredDate;
    private String notes;
    private LocalDateTime createdAt;

    public WateringRecordResponse() {
    }

    public WateringRecordResponse(Long id, Long plantId, LocalDate wateredDate, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.plantId = plantId;
        this.wateredDate = wateredDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static WateringRecordResponse fromEntity(WateringRecord record) {
        if (record == null) return null;
        return new WateringRecordResponse(
                record.getId(),
                record.getPlant() != null ? record.getPlant().getId() : null,
                record.getWateredDate(),
                record.getNotes(),
                record.getCreatedAt()
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}