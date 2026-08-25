package com.plantpal.dto.response;

import com.plantpal.entity.GrowthRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GrowthRecordResponse {

    private Long id;
    private Long plantId;
    private LocalDate recordDate;
    private BigDecimal heightCm;
    private Integer leafCount;
    private String notes;
    private LocalDateTime createdAt;

    public GrowthRecordResponse() {
    }

    public GrowthRecordResponse(Long id, Long plantId, LocalDate recordDate, BigDecimal heightCm,
                                Integer leafCount, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.plantId = plantId;
        this.recordDate = recordDate;
        this.heightCm = heightCm;
        this.leafCount = leafCount;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static GrowthRecordResponse fromEntity(GrowthRecord record) {
        if (record == null) return null;
        return new GrowthRecordResponse(
                record.getId(),
                record.getPlant() != null ? record.getPlant().getId() : null,
                record.getRecordDate(),
                record.getHeightCm(),
                record.getLeafCount(),
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}