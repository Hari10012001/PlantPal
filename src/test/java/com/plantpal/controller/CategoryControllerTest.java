package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.CategoryRequest;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.Role;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlantCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        // Seed 3 standard categories for test consistency
        categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
        categoryRepository.save(new PlantCategory("Succulent", "Water-storing plants"));
        categoryRepository.save(new PlantCategory("Fern", "Shade plants"));
    }

    @Test
    @DisplayName("FR-CAT-04: Authenticated USER can view all categories")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testGetAllCategories_UserRole_Success() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Herb", "Succulent", "Fern")));
    }

    @Test
    @DisplayName("GET /api/categories/{id}: Get single category by ID")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testGetCategoryById_Success() throws Exception {
        PlantCategory herb = categoryRepository.findByNameIgnoreCase("Herb").orElseThrow();

        mockMvc.perform(get("/api/categories/" + herb.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(herb.getId()))
                .andExpect(jsonPath("$.name").value("Herb"))
                .andExpect(jsonPath("$.description").value("Cooking herbs"));
    }

    @Test
    @DisplayName("GET /api/categories/{id}: Non-existent category returns 404")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testGetCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/api/categories/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with id: 99999"));
    }

    @Test
    @DisplayName("FR-CAT-01: Admin can create a new category")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminCreateCategory_Success() throws Exception {
        CategoryRequest request = new CategoryRequest("Cactus", "Desert spiny plants");

        mockMvc.perform(post("/api/admin/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Cactus"))
                .andExpect(jsonPath("$.description").value("Desert spiny plants"));
    }

    @Test
    @DisplayName("Validation: Creating category with duplicate name returns 409 Conflict")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminCreateCategory_DuplicateName() throws Exception {
        CategoryRequest request = new CategoryRequest("herb", "Duplicate herb");

        mockMvc.perform(post("/api/admin/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category name already exists: herb"));
    }

    @Test
    @DisplayName("Validation: Creating category with blank name returns 400 Bad Request")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminCreateCategory_ValidationError() throws Exception {
        CategoryRequest request = new CategoryRequest("", "Empty name");

        mockMvc.perform(post("/api/admin/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("FR-CAT-02: Admin can update an existing category")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminUpdateCategory_Success() throws Exception {
        PlantCategory fern = categoryRepository.findByNameIgnoreCase("Fern").orElseThrow();
        CategoryRequest updateRequest = new CategoryRequest("Ferns & Mosses", "Updated description");

        mockMvc.perform(put("/api/admin/categories/" + fern.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fern.getId()))
                .andExpect(jsonPath("$.name").value("Ferns & Mosses"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("FR-CAT-03: Admin can delete an unused category")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminDeleteCategory_Success() throws Exception {
        PlantCategory succulent = categoryRepository.findByNameIgnoreCase("Succulent").orElseThrow();

        mockMvc.perform(delete("/api/admin/categories/" + succulent.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.existsById(succulent.getId()));
    }

    @Test
    @DisplayName("Authorization: Regular USER cannot access /api/admin/categories (Expect 403 Forbidden)")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testUserRole_AccessAdminEndpoints_Forbidden() throws Exception {
        CategoryRequest request = new CategoryRequest("Tree", "Bonsai and fruit trees");

        mockMvc.perform(post("/api/admin/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/admin/categories/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Authorization: Unauthenticated request to /api/categories returns 401 Unauthorized")
    void testUnauthenticated_AccessCategories_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }
}