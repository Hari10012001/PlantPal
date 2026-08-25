package com.plantpal.repository;

import com.plantpal.entity.CareSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareScheduleRepository extends JpaRepository<CareSchedule, Long> {

    Optional<CareSchedule> findByPlantId(Long plantId);

    Optional<CareSchedule> findByPlantIdAndPlantUserId(Long plantId, Long userId);

    boolean existsByPlantId(Long plantId);
}