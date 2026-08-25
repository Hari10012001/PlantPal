package com.plantpal.entity;

import com.plantpal.enums.SunlightNeeds;
import com.plantpal.enums.WateringStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "care_schedules")
public class CareSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plant_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plant plant;

    @Column(name = "watering_interval_days", nullable = false)
    private Integer wateringIntervalDays = 7;

    @Column(name = "last_watered_date")
    private LocalDate lastWateredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sunlight_needs", length = 30)
    private SunlightNeeds sunlightNeeds;

    @Column(name = "fertilizing_interval_days")
    private Integer fertilizingIntervalDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CareSchedule() {
    }

    public CareSchedule(Plant plant, Integer wateringIntervalDays, LocalDate lastWateredDate,
                        SunlightNeeds sunlightNeeds, Integer fertilizingIntervalDays) {
        this.plant = plant;
        this.wateringIntervalDays = (wateringIntervalDays != null && wateringIntervalDays >= 1) ? wateringIntervalDays : 7;
        this.lastWateredDate = lastWateredDate;
        this.sunlightNeeds = sunlightNeeds;
        this.fertilizingIntervalDays = fertilizingIntervalDays;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.wateringIntervalDays == null || this.wateringIntervalDays < 1) {
            this.wateringIntervalDays = 7;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Computed Next Watering Date (NOT stored in DB).
     * Returns null if lastWateredDate is null.
     */
    @Transient
    public LocalDate getNextWateringDate() {
        if (lastWateredDate == null) {
            return null;
        }
        int interval = (wateringIntervalDays != null && wateringIntervalDays >= 1) ? wateringIntervalDays : 7;
        return lastWateredDate.plusDays(interval);
    }

    /**
     * Computed Watering Status (NOT stored in DB).
     * If lastWateredDate is null -> NOT_SET
     * If nextWateringDate < today -> WATER_OVERDUE
     * If nextWateringDate == today -> WATER_TODAY
     * If nextWateringDate > today -> WATER_UPCOMING
     */
    @Transient
    public WateringStatus getWateringStatus() {
        if (lastWateredDate == null) {
            return WateringStatus.NOT_SET;
        }
        LocalDate nextDate = getNextWateringDate();
        if (nextDate == null) {
            return WateringStatus.NOT_SET;
        }

        LocalDate today = LocalDate.now();
        if (nextDate.isBefore(today)) {
            return WateringStatus.WATER_OVERDUE;
        } else if (nextDate.isEqual(today)) {
            return WateringStatus.WATER_TODAY;
        } else {
            return WateringStatus.WATER_UPCOMING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
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