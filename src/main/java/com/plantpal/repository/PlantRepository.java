package com.plantpal.repository;

import com.plantpal.entity.Plant;
import com.plantpal.enums.PlantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

    List<Plant> findByUserId(Long userId);

    Optional<Plant> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    boolean existsByCategoryId(Long categoryId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, PlantStatus status);

    @Query("SELECT p FROM Plant p WHERE p.user.id = :userId " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(p.species) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(p.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Plant> searchUserPlants(@Param("userId") Long userId,
                                 @Param("categoryId") Long categoryId,
                                 @Param("status") PlantStatus status,
                                 @Param("search") String search);
}