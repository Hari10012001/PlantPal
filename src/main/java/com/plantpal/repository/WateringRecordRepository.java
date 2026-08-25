package com.plantpal.repository;

import com.plantpal.entity.WateringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WateringRecordRepository extends JpaRepository<WateringRecord, Long> {

    List<WateringRecord> findByPlantIdOrderByWateredDateDescCreatedAtDesc(Long plantId);

    List<WateringRecord> findByPlantIdAndPlantUserIdOrderByWateredDateDescCreatedAtDesc(Long plantId, Long userId);

    void deleteByPlantId(Long plantId);
}