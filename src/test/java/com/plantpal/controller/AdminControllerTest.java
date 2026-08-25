package com.plantpal.controller;

import com.plantpal.entity.*;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.Role;
import com.plantpal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    private User admin;
    private User alice;
    private User bob;
    private PlantCategory herbCategory;
    private PlantCategory succulentCategory;

    @BeforeEach
    void setUp() {
        growthRecordRepository.deleteAll();
        wateringRecordRepository.deleteAll();
        careScheduleRepository.deleteAll();
        plantRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        admin = userRepository.save(new User("Admin System", "admin@plantpal.local", passwordEncoder.encode("AdminPass123!"), Role.ADMIN));
        alice = userRepository.save(new User("Alice Smith", "alice@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        bob = userRepository.save(new User("Bob Jones", "bob@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));

        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
        succulentCategory = categoryRepository.save(new PlantCategory("Succulent", "Fleshy desert plants"));

        // Alice has 2 plants
        Plant p1 = plantRepository.save(new Plant(alice, herbCategory, "Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        Plant p2 = plantRepository.save(new Plant(alice, succulentCategory, "Aloe", "Aloe", "Desc", "Room", PlantStatus.NEEDS_ATTENTION));

        // Bob has 1 plant
        Plant p3 = plantRepository.save(new Plant(bob, herbCategory, "Mint", "Mentha", "Desc", "Kitchen", PlantStatus.INACTIVE));

        // Watering and Growth records
        wateringRecordRepository.save(new WateringRecord(p1, LocalDate.now(), "W1"));
        growthRecordRepository.save(new GrowthRecord(p1, LocalDate.now(), new BigDecimal("12.50"), 6, "G1"));
    }

    @Test
    @DisplayName("Endpoint 22: GET /api/admin/users returns all users overview for ADMIN")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testGetAllUsers_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email").value("admin@plantpal.local"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[0].plantCount").value(0))
                .andExpect(jsonPath("$[1].email").value("alice@plantpal.local"))
                .andExpect(jsonPath("$[1].plantCount").value(2))
                .andExpect(jsonPath("$[2].email").value("bob@plantpal.local"))
                .andExpect(jsonPath("$[2].plantCount").value(1));
    }

    @Test
    @DisplayName("Security: Normal USER cannot access GET /api/admin/users (Expect 403 Forbidden)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetAllUsers_NormalUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Endpoint 27: GET /api/admin/stats returns platform statistics for ADMIN")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testGetSystemStats_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(3))
                .andExpect(jsonPath("$.totalPlants").value(3))
                .andExpect(jsonPath("$.totalWateringRecords").value(1))
                .andExpect(jsonPath("$.totalGrowthRecords").value(1))
                .andExpect(jsonPath("$.totalCategories").value(2))
                .andExpect(jsonPath("$.plantsByStatus.HEALTHY").value(1))
                .andExpect(jsonPath("$.plantsByStatus.NEEDS_ATTENTION").value(1))
                .andExpect(jsonPath("$.plantsByStatus.INACTIVE").value(1));
    }

    @Test
    @DisplayName("Security: Normal USER cannot access GET /api/admin/stats (Expect 403 Forbidden)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetSystemStats_NormalUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access to /api/admin endpoints returns 401 Unauthorized")
    void testAdminEndpoints_Unauthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }
}