package com.plantpal.service;

import com.plantpal.dto.request.CategoryRequest;
import com.plantpal.dto.response.CategoryResponse;
import com.plantpal.entity.PlantCategory;
import com.plantpal.exception.DuplicateResourceException;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.PlantCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final PlantCategoryRepository categoryRepository;

    public CategoryService(PlantCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String trimmedName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Category name already exists: " + trimmedName);
        }

        PlantCategory category = new PlantCategory(
                trimmedName,
                request.getDescription() != null ? request.getDescription().trim() : null
        );

        PlantCategory saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        String trimmedName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Category name already exists: " + trimmedName);
        }

        category.setName(trimmedName);
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        PlantCategory updated = categoryRepository.save(category);
        return CategoryResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Category-in-use protection: in M4 when plants table is added, foreign key RESTRICT
        // and service-level checks will prevent deletion of referenced categories.
        categoryRepository.delete(category);
    }
}