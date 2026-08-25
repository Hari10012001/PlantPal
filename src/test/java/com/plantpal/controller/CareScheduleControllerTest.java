package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.CareScheduleRequest;
import com.plantpal.dto.request.PlantRequest;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.Role;
import com.plantpal.enums.SunlightNeeds;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.PlantRepository;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CareScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private User bob;
    private PlantCategory herbCategory;

    @BeforeEach
    void setUp() {
        careScheduleRepository.deleteAll();
        plantRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        alice = userRepository.save(new User("Alice Smith", "alice@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        bob = userRepository.save(new User("Bob Jones", "bob@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
    }

    @Test
    @DisplayName("Auto-Creation: Creating a plant automatically creates a CareSchedule with NOT_SET status")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testPlantCreation_AutoCreatesCareSchedule() throws Exception {
        PlantRequest request = new PlantRequest(
                "Sweet Basil",
                herbCategory.getId(),
                "Ocimum",
                "Desc",
                "Kitchen",
                PlantStatus.HEALTHY,
                5,
                null,
                SunlightNeeds.FULL_SUN,
                14
        );

        mockMvc.perform(post("/api/plants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.careSchedule.wateringIntervalDays").value(5))
                .andExpect(jsonPath("$.careSchedule.wateringStatus").value("NOT_SET"))
                .andExpect(jsonPath("$.careSchedule.sunlightNeeds").value("FULL_SUN"))
                .andExpect(jsonPath("$.careSchedule.fertilizingIntervalDays").value(14));
    }

    @Test
    @DisplayName("Endpoint 12: GET /api/plants/{id}/care returns CareSchedule for owner")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetCareSchedule_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Rosemary", "Salvia", "Desc", "Balcony", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(plant, 7, LocalDate.now().minusDays(2), SunlightNeeds.FULL_SUN, 30));

        mockMvc.perform(get("/api/plants/" + plant.getId() + "/care"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantId").value(plant.getId()))
                .andExpect(jsonPath("$.wateringIntervalDays").value(7))
                .andExpect(jsonPath("$.lastWateredDate").value(LocalDate.now().minusDays(2).toString()))
                .andExpect(jsonPath("$.nextWateringDate").value(LocalDate.now().plusDays(5).toString()))
                .andExpect(jsonPath("$.wateringStatus").value("WATER_UPCOMING"))
                .andExpect(jsonPath("$.sunlightNeeds").value("FULL_SUN"));
    }

    @Test
    @DisplayName("Watering Status Logic: WATER_TODAY when nextWateringDate equals today")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testCareSchedule_WateringStatus_WaterToday() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Thyme", "Thymus", "Desc", "Garden", PlantStatus.HEALTHY));
        // Interval = 3 days, last watered 3 days ago -> next watering is today
        careScheduleRepository.save(new CareSchedule(plant, 3, LocalDate.now().minusDays(3), SunlightNeeds.FULL_SUN, null));

        mockMvc.perform(get("/api/plants/" + plant.getId() + "/care"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextWateringDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.wateringStatus").value("WATER_TODAY"));
    }

    @Test
    @DisplayName("Watering Status Logic: WATER_OVERDUE when nextWateringDate is in the past")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testCareSchedule_WateringStatus_WaterOverdue() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Mint", "Mentha", "Desc", "Pot", PlantStatus.HEALTHY));
        // Interval = 5 days, last watered 8 days ago -> next watering was 3 days ago (overdue)
        careScheduleRepository.save(new CareSchedule(plant, 5, LocalDate.now().minusDays(8), SunlightNeeds.PARTIAL_SUN, null));

        mockMvc.perform(get("/api/plants/" + plant.getId() + "/care"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextWateringDate").value(LocalDate.now().minusDays(3).toString()))
                .andExpect(jsonPath("$.wateringStatus").value("WATER_OVERDUE"));
    }

    @Test
    @DisplayName("Endpoint 13: PUT /api/plants/{id}/care updates care schedule")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateCareSchedule_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(plant, 7, null, null, null));

        CareScheduleRequest updateRequest = new CareScheduleRequest(
                4,
                LocalDate.now().minusDays(1),
                SunlightNeeds.SHADE,
                21
        );

        mockMvc.perform(put("/api/plants/" + plant.getId() + "/care")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wateringIntervalDays").value(4))
                .andExpect(jsonPath("$.lastWateredDate").value(LocalDate.now().minusDays(1).toString()))
                .andExpect(jsonPath("$.nextWateringDate").value(LocalDate.now().plusDays(3).toString()))
                .andExpect(jsonPath("$.wateringStatus").value("WATER_UPCOMING"))
                .andExpect(jsonPath("$.sunlightNeeds").value("SHADE"))
                .andExpect(jsonPath("$.fertilizingIntervalDays").value(21));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot view care schedule (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testGetCareSchedule_NonOwner_Returns404() throws Exception {
        Plant alicesPlant = plantRepository.save(new Plant(alice, herbCategory, "Alice's Sage", "Salvia", "Desc", "Roof", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(alicesPlant, 7, null, null, null));

        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/care"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + alicesPlant.getId()));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot update care schedule (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testUpdateCareSchedule_NonOwner_Returns404() throws Exception {
        Plant alicesPlant = plantRepository.save(new Plant(alice, herbCategory, "Alice's Sage", "Salvia", "Desc", "Roof", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(alicesPlant, 7, null, null, null));

        CareScheduleRequest updateReq = new CareScheduleRequest(3, LocalDate.now(), SunlightNeeds.FULL_SUN, null);

        mockMvc.perform(put("/api/plants/" + alicesPlant.getId() + "/care")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Validation: Future lastWateredDate returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateCareSchedule_FutureDate_BadRequest() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Oregano", "Origanum", "Desc", "Pot", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(plant, 7, null, null, null));

        CareScheduleRequest updateReq = new CareScheduleRequest(
                7,
                LocalDate.now().plusDays(5), // Future date
                SunlightNeeds.FULL_SUN,
                null
        );

        mockMvc.perform(put("/api/plants/" + plant.getId() + "/care")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.lastWateredDate").exists());
    }

    @Test
    @DisplayName("Validation: Invalid watering interval (< 1 or > 365) returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdateCareSchedule_InvalidInterval_BadRequest() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Dill", "Anethum", "Desc", "Pot", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(plant, 7, null, null, null));

        // Test < 1
        CareScheduleRequest tooSmall = new CareScheduleRequest(0, null, null, null);
        mockMvc.perform(put("/api/plants/" + plant.getId() + "/care")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooSmall)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.wateringIntervalDays").exists());

        // Test > 365
        CareScheduleRequest tooLarge = new CareScheduleRequest(366, null, null, null);
        mockMvc.perform(put("/api/plants/" + plant.getId() + "/care")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooLarge)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.wateringIntervalDays").exists());
    }

    @Test
    @DisplayName("Cascade Deletion: Deleting a Plant automatically deletes its associated CareSchedule")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testPlantDeletion_CascadesToCareSchedule() throws Exception {
        Plant plant = plantRepository.save(new Plant(alice, herbCategory, "Cascade Test Plant", "Ocimum", "Desc", "Room", PlantStatus.HEALTHY));
        CareSchedule schedule = careScheduleRepository.save(new CareSchedule(plant, 7, LocalDate.now(), SunlightNeeds.FULL_SUN, 14));
        Long scheduleId = schedule.getId();

        assertTrue(careScheduleRepository.existsById(scheduleId));

        mockMvc.perform(delete("/api/plants/" + plant.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(plantRepository.existsById(plant.getId()));
        assertFalse(careScheduleRepository.existsById(scheduleId));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access to /api/plants/{id}/care returns 401")
    void testUnauthenticated_AccessCareSchedule_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/plants/1/care"))
                .andExpect(status().isUnauthorized());
    }
}