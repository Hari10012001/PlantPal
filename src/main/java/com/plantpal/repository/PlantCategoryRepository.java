package com.plantpal.repository;

import com.plantpal.entity.PlantCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantCategoryRepository extends JpaRepository<PlantCategory, Long> {

    Optional<PlantCategory> findByName(String name);

    Optional<PlantCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}