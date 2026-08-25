package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.CategoryRequest;
import com.plantpal.entity.PlantCategory;
import com.plantpal.repository.PlantCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        // Seed 3 standard categories for test consistency
        categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
        categoryRepository.save(new PlantCategory("Succulent", "Water-storing plants"));
        categoryRepository.save(new PlantCategory("Fern", "Shade plants"));
    }

    @Test
    @DisplayName("FR-CAT-04: Authenticated USER can view all categories via GET /api/categories")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testGetAllCategories_UserRole_Success() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Herb", "Succulent", "Fern")));
    }

    @Test
    @DisplayName("FR-CAT-04: Authenticated ADMIN can view all categories via GET /api/categories")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testGetAllCategories_AdminRole_Success() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated request to /api/categories returns 401 Unauthorized")
    void testUnauthenticated_AccessCategories_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("FR-ADMIN-02: Admin can view all categories via GET /api/admin/categories")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminGetAllCategories_Success() throws Exception {
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("FR-CAT-01: Admin can create a new category via POST /api/admin/categories")
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
    @DisplayName("FR-CAT-02: Admin can update an existing category via PUT /api/admin/categories/{id}")
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
    @DisplayName("Validation: Updating category with duplicate name of another category returns 409 Conflict")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminUpdateCategory_DuplicateName() throws Exception {
        PlantCategory fern = categoryRepository.findByNameIgnoreCase("Fern").orElseThrow();
        CategoryRequest updateRequest = new CategoryRequest("Herb", "Renaming to existing herb");

        mockMvc.perform(put("/api/admin/categories/" + fern.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category name already exists: Herb"));
    }

    @Test
    @DisplayName("FR-CAT-03: Admin can delete an unused category via DELETE /api/admin/categories/{id}")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminDeleteCategory_Success() throws Exception {
        PlantCategory succulent = categoryRepository.findByNameIgnoreCase("Succulent").orElseThrow();

        mockMvc.perform(delete("/api/admin/categories/" + succulent.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.existsById(succulent.getId()));
    }

    @Test
    @DisplayName("Error Handling: Deleting non-existent category returns 404 Not Found")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testAdminDeleteCategory_NotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/categories/99999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with id: 99999"));
    }

    @Test
    @DisplayName("Authorization: Regular USER cannot access /api/admin/categories (Expect 403 Forbidden)")
    @WithMockUser(username = "user@plantpal.local", roles = {"USER"})
    void testUserRole_AccessAdminEndpoints_Forbidden() throws Exception {
        CategoryRequest request = new CategoryRequest("Tree", "Bonsai and fruit trees");

        // GET admin
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isForbidden());

        // POST admin
        mockMvc.perform(post("/api/admin/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // DELETE admin
        mockMvc.perform(delete("/api/admin/categories/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}