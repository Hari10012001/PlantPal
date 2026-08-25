package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.ChangePasswordRequest;
import com.plantpal.dto.request.UpdateProfileRequest;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.Role;
import com.plantpal.repository.*;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GrowthRecordRepository growthRecordRepository;

    @Autowired
    private WateringRecordRepository wateringRecordRepository;

    @Autowired
    private CareScheduleRepository careScheduleRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User alice;
    private PlantCategory herbCategory;

    @BeforeEach
    void setUp() {
        growthRecordRepository.deleteAll();
        wateringRecordRepository.deleteAll();
        careScheduleRepository.deleteAll();
        plantRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        alice = userRepository.save(new User("Alice Smith", "alice@plantpal.local", passwordEncoder.encode("OldPass123!"), Role.USER));
        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
        plantRepository.save(new Plant(alice, herbCategory, "Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
    }

    @Test
    @DisplayName("Endpoint 19: GET /api/profile returns authenticated user profile with plant count")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetProfile_Success() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alice.getId()))
                .andExpect(jsonPath("$.fullName").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@plantpal.local"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.totalPlants").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("Endpoint 20: PUT /api/profile with CSRF updates user's full name")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Alice Elizabeth Smith");

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice Elizabeth Smith"))
                .andExpect(jsonPath("$.email").value("alice@plantpal.local"));
    }

    @Test
    @DisplayName("CSRF Security: PUT /api/profile without CSRF token returns 403 Forbidden")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateProfile_WithoutCsrf_Forbidden() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Hacked Profile Name");

        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Validation: PUT /api/profile with blank name returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateProfile_BlankName_BadRequest() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("");

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fullName").exists());
    }

    @Test
    @DisplayName("Endpoint 21: PUT /api/profile/password with CSRF successfully changes user password")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPass123!",
                "NewBrandPassword@2026",
                "NewBrandPassword@2026"
        );

        mockMvc.perform(put("/api/profile/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        User updatedUser = userRepository.findById(alice.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("NewBrandPassword@2026", updatedUser.getPasswordHash()));
    }

    @Test
    @DisplayName("CSRF Security: PUT /api/profile/password without CSRF token returns 403 Forbidden")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testChangePassword_WithoutCsrf_Forbidden() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPass123!",
                "NewBrandPassword@2026",
                "NewBrandPassword@2026"
        );

        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Validation: Incorrect current password returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testChangePassword_IncorrectCurrentPassword_BadRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "WrongCurrentPass",
                "NewBrandPassword@2026",
                "NewBrandPassword@2026"
        );

        mockMvc.perform(put("/api/profile/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }

    @Test
    @DisplayName("Validation: Mismatched new passwords returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testChangePassword_MismatchedConfirmPassword_BadRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPass123!",
                "NewBrandPassword@2026",
                "DifferentPassword@2026"
        );

        mockMvc.perform(put("/api/profile/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New passwords do not match"));
    }

    @Test
    @DisplayName("Validation: New password less than 6 chars returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testChangePassword_ShortPassword_BadRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "OldPass123!",
                "12345",
                "12345"
        );

        mockMvc.perform(put("/api/profile/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.newPassword").exists());
    }

    @Test
    @DisplayName("Authorization: Unauthenticated profile access returns 401 Unauthorized")
    void testProfile_Unauthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateProfileRequest("New Name"))))
                .andExpect(status().isUnauthorized());
    }
}