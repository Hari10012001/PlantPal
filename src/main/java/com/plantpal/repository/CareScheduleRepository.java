package com.plantpal.repository;

import com.plantpal.entity.CareSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareScheduleRepository extends JpaRepository<CareSchedule, Long> {

    Optional<CareSchedule> findByPlantId(Long plantId);

    Optional<CareSchedule> findByPlantIdAndPlantUserId(Long plantId, Long userId);

    boolean existsByPlantId(Long plantId);

    @Query("SELECT cs FROM CareSchedule cs JOIN FETCH cs.plant p JOIN FETCH p.category c WHERE p.user.id = :userId")
    List<CareSchedule> findByUserIdWithPlantAndCategory(@Param("userId") Long userId);
}