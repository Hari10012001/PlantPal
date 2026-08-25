package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.GrowthRecordRequest;
import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.GrowthRecord;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
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

import java.math.BigDecimal;
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
class GrowthRecordControllerTest {

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
    @DisplayName("FR-GROWTH-01: Owner can record growth with all fields (POST /api/plants/{id}/growth)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_Owner_AllFields_Success() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(
                LocalDate.now(),
                new BigDecimal("14.50"),
                8,
                "Strong stem and green leaves"
        );

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.plantId").value(alicesPlant.getId()))
                .andExpect(jsonPath("$.recordDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.heightCm").value(14.50))
                .andExpect(jsonPath("$.leafCount").value(8))
                .andExpect(jsonPath("$.notes").value("Strong stem and green leaves"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("FR-GROWTH-02: Owner can record growth with only height provided")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_OnlyHeight_Success() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(
                LocalDate.now(),
                new BigDecimal("12.00"),
                null,
                null
        );

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.heightCm").value(12.00))
                .andExpect(jsonPath("$.leafCount").doesNotExist());
    }

    @Test
    @DisplayName("FR-GROWTH-02: Owner can record growth with only notes provided")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_OnlyNotes_Success() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(
                LocalDate.now(),
                null,
                null,
                "Observed flower buds forming"
        );

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes").value("Observed flower buds forming"));
    }

    @Test
    @DisplayName("FR-GROWTH-03: Owner can retrieve full growth history ordered newest first")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetGrowthHistory_Owner_Success() throws Exception {
        LocalDate d1 = LocalDate.now().minusDays(10);
        LocalDate d2 = LocalDate.now().minusDays(5);
        LocalDate d3 = LocalDate.now();

        growthRecordRepository.save(new GrowthRecord(alicesPlant, d1, new BigDecimal("5.00"), 2, "Early seedling"));
        growthRecordRepository.save(new GrowthRecord(alicesPlant, d3, new BigDecimal("15.00"), 8, "Flourishing (newest)"));
        growthRecordRepository.save(new GrowthRecord(alicesPlant, d2, new BigDecimal("10.00"), 4, "Growing well"));

        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/growth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].recordDate").value(d3.toString()))
                .andExpect(jsonPath("$[1].recordDate").value(d2.toString()))
                .andExpect(jsonPath("$[2].recordDate").value(d1.toString()));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot view growth history (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testGetGrowthHistory_NonOwner_Returns404() throws Exception {
        growthRecordRepository.save(new GrowthRecord(alicesPlant, LocalDate.now(), new BigDecimal("10.00"), 4, "Alice private record"));

        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/growth"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + alicesPlant.getId()));
    }

    @Test
    @DisplayName("Ownership Isolation: Non-owner cannot record growth (Expect 404 Not Found)")
    @WithMockUser(username = "bob@plantpal.local", roles = {"USER"})
    void testRecordGrowth_NonOwner_Returns404() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(LocalDate.now(), new BigDecimal("10.00"), 4, "Hacking growth");

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + alicesPlant.getId()));
    }

    @Test
    @DisplayName("Validation: Future recordDate returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_FutureDate_BadRequest() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(
                LocalDate.now().plusDays(2),
                new BigDecimal("10.00"),
                4,
                "Future growth"
        );

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.recordDate").exists());
    }

    @Test
    @DisplayName("Validation: Missing all observation fields returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_AllFieldsEmpty_BadRequest() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(LocalDate.now(), null, null, null);

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one of height, leaf count, or notes must be provided"));
    }

    @Test
    @DisplayName("Validation: Height <= 0 returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_InvalidHeight_BadRequest() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(LocalDate.now(), new BigDecimal("0.00"), null, null);

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.heightCm").exists());
    }

    @Test
    @DisplayName("Validation: Negative leafCount returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testRecordGrowth_NegativeLeafCount_BadRequest() throws Exception {
        GrowthRecordRequest request = new GrowthRecordRequest(LocalDate.now(), null, -1, null);

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.leafCount").exists());
    }

    @Test
    @DisplayName("Cascade Deletion: Deleting a Plant deletes all its GrowthRecords")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDeletePlant_CascadesToGrowthRecords() throws Exception {
        GrowthRecord record1 = growthRecordRepository.save(new GrowthRecord(alicesPlant, LocalDate.now(), new BigDecimal("10.00"), 4, "G1"));
        GrowthRecord record2 = growthRecordRepository.save(new GrowthRecord(alicesPlant, LocalDate.now().minusDays(1), new BigDecimal("9.00"), 3, "G2"));

        assertTrue(growthRecordRepository.existsById(record1.getId()));
        assertTrue(growthRecordRepository.existsById(record2.getId()));

        mockMvc.perform(delete("/api/plants/" + alicesPlant.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(plantRepository.existsById(alicesPlant.getId()));
        assertFalse(growthRecordRepository.existsById(record1.getId()));
        assertFalse(growthRecordRepository.existsById(record2.getId()));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access returns 401 Unauthorized")
    void testUnauthenticated_AccessGrowth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/plants/" + alicesPlant.getId() + "/growth"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/plants/" + alicesPlant.getId() + "/growth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GrowthRecordRequest(LocalDate.now(), new BigDecimal("10.00"), null, null))))
                .andExpect(status().isUnauthorized());
    }
}