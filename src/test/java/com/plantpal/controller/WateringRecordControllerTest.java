package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.WateringRecordRequest;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.entity.WateringRecord;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.Role;
import com.plantpal.repository.CareScheduleRepository;
import com.plantpal.repository.GrowthRecordRepository;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.PlantRepository;
import com.plantpal.repository.UserRepository;
import com.plantpal.repository.WateringRecordRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WateringRecordControllerTest {

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
    private User bob;
    private PlantCategory herbCategory;
    private Plant alicesPlant;

    @BeforeEach
    void setUp() {
        growthRecordRepository.deleteAll();
        wateringRecordRepository.deleteAll();
        careScheduleRepository.deleteAll();
        plantRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        alice = userRepository.save(new User("Alice Smith", "alice@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        bob = userRepository.save(new User("Bob Jones", "bob@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));

        alicesPlant = plantRepository.save(new Plant(alice, herbCategory, "Sweet Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(alicesPlant, 5, null, null, null));
    }

    @Test
    @DisplayName("FR-WATER-01: Owner can record a watering event (POST /api/plants/{id}/watering)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordWatering_Owner_Success() throws Exception {
        WateringRecordRequest request = new WateringRecordRequest(LocalDate.now(), "Morning watering");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.plantId").value(alicesPlant.getId()))
                .andExpect(jsonPath("$.wateredDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.notes").value("Morning watering"))
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify side effect: care_schedules.last_watered_date is updated
        CareSchedule schedule = careScheduleRepository.findByPlantId(alicesPlant.getId()).orElseThrow();
        assertEquals(LocalDate.now(), schedule.getLastWateredDate());
    }

    @Test
    @DisplayName("FR-WATER-02: Recording older watering date does NOT overwrite newer last_watered_date")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordWatering_OlderDate_DoesNotOverwriteNewer() throws Exception {
        // Set care schedule last watered to yesterday
        LocalDate yesterday = LocalDate.now().minusDays(1);
        CareSchedule schedule = careScheduleRepository.findByPlantId(alicesPlant.getId()).orElseThrow();
        schedule.setLastWateredDate(yesterday);
        careScheduleRepository.save(schedule);

        // Record a watering event for 5 days ago (older historical entry)
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        WateringRecordRequest olderRequest = new WateringRecordRequest(fiveDaysAgo, "Older watering");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(olderRequest)))
                .andExpect(status().isCreated());

        // Verify care_schedules.last_watered_date is STILL yesterday
        CareSchedule updatedSchedule = careScheduleRepository.findByPlantId(alicesPlant.getId()).orElseThrow();
        assertEquals(yesterday, updatedSchedule.getLastWateredDate());
    }

    @Test
    @DisplayName("FR-WATER-04: Owner can retrieve full watering history ordered newest first")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetWateringHistory_Owner_Success() throws Exception {
        LocalDate d1 = LocalDate.now().minusDays(3);
        LocalDate d2 = LocalDate.now().minusDays(1);
        LocalDate d3 = LocalDate.now();

        wateringRecordRepository.save(new WateringRecord(alicesPlant, d1, "First watering"));
        wateringRecordRepository.save(new WateringRecord(alicesPlant, d3, "Third watering (newest)"));
        wateringRecordRepository.save(new WateringRecord(alicesPlant, d2, "Second watering"));

        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/watering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].wateredDate").value(d3.toString()))
                .andExpect(jsonPath("$[1].wateredDate").value(d2.toString()))
                .andExpect(jsonPath("$[2].wateredDate").value(d1.toString()));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot view watering history (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testGetWateringHistory_NonOwner_Returns404() throws Exception {
        wateringRecordRepository.save(new WateringRecord(alicesPlant, LocalDate.now(), "Alice secret record"));

        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/watering"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + alicesPlant.getId()));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot record watering (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testRecordWatering_NonOwner_Returns404() throws Exception {
        WateringRecordRequest request = new WateringRecordRequest(LocalDate.now(), "Hacking watering");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + alicesPlant.getId()));
    }

    @Test
    @DisplayName("Validation: Future wateredDate returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordWatering_FutureDate_BadRequest() throws Exception {
        WateringRecordRequest request = new WateringRecordRequest(LocalDate.now().plusDays(2), "Future watering");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.wateredDate").exists());
    }

    @Test
    @DisplayName("Validation: Null wateredDate returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordWatering_NullDate_BadRequest() throws Exception {
        WateringRecordRequest request = new WateringRecordRequest(null, "Null date");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.wateredDate").exists());
    }

    @Test
    @DisplayName("Validation: Notes > 500 characters returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordWatering_NotesTooLong_BadRequest() throws Exception {
        String longNotes = "A".repeat(501);
        WateringRecordRequest request = new WateringRecordRequest(LocalDate.now(), longNotes);

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.notes").exists());
    }

    @Test
    @DisplayName("Cascade Deletion: Deleting a Plant deletes all its WateringRecords")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDeletePlant_CascadesToWateringRecords() throws Exception {
        WateringRecord record1 = wateringRecordRepository.save(new WateringRecord(alicesPlant, LocalDate.now(), "R1"));
        WateringRecord record2 = wateringRecordRepository.save(new WateringRecord(alicesPlant, LocalDate.now().minusDays(1), "R2"));

        assertTrue(wateringRecordRepository.existsById(record1.getId()));
        assertTrue(wateringRecordRepository.existsById(record2.getId()));

        mockMvc.perform(delete("/api/plants/" + alicesPlant.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(plantRepository.existsById(alicesPlant.getId()));
        assertFalse(wateringRecordRepository.existsById(record1.getId()));
        assertFalse(wateringRecordRepository.existsById(record2.getId()));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access returns 401 Unauthorized")
    void testUnauthenticated_AccessWatering_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/watering"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/watering")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WateringRecordRequest(LocalDate.now(), "notes"))))
                .andExpect(status().isUnauthorized());
    }
}