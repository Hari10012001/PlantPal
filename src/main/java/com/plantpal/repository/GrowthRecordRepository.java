package com.plantpal.repository;

import com.plantpal.entity.GrowthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrowthRecordRepository extends JpaRepository<GrowthRecord, Long> {

    List<GrowthRecord> findByPlantIdOrderByRecordDateDescCreatedAtDesc(Long plantId);

    List<GrowthRecord> findByPlantIdAndPlantUserIdOrderByRecordDateDescCreatedAtDesc(Long plantId, Long userId);

    void deleteByPlantId(Long plantId);
}