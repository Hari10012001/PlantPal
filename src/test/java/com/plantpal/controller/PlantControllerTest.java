package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.PlantRequest;
import com.plantpal.dto.request.PlantStatusRequest;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlantControllerTest {

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

    private User userA;
    private User userB;
    private User admin;
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

        // 1. Create test users
        userA = userRepository.save(new User("Alice Gardner", "alice@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        userB = userRepository.save(new User("Bob Planter", "bob@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        admin = userRepository.save(new User("Admin System", "admin@plantpal.local", passwordEncoder.encode("AdminPass123!"), Role.ADMIN));

        // 2. Create test categories
        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Culinary kitchen herbs"));
        succulentCategory = categoryRepository.save(new PlantCategory("Succulent", "Fleshy desert plants"));
    }

    @Test
    @DisplayName("FR-PLANT-01: Authenticated user can create a plant (201 Created)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testCreatePlant_Success() throws Exception {
        PlantRequest request = new PlantRequest(
                "Sweet Basil",
                herbCategory.getId(),
                "Ocimum basilicum",
                "Italian cooking herb",
                "Kitchen Window",
                PlantStatus.HEALTHY
        );

        mockMvc.perform(post("/api/plants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Sweet Basil"))
                .andExpect(jsonPath("$.species").value("Ocimum basilicum"))
                .andExpect(jsonPath("$.location").value("Kitchen Window"))
                .andExpect(jsonPath("$.status").value("HEALTHY"))
                .andExpect(jsonPath("$.category.id").value(herbCategory.getId()))
                .andExpect(jsonPath("$.category.name").value("Herb"));
    }

    @Test
    @DisplayName("Validation: Creating plant with invalid category returns 404 Not Found")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testCreatePlant_InvalidCategory_NotFound() throws Exception {
        PlantRequest request = new PlantRequest(
                "Ghost Plant",
                99999L,
                "Species",
                "Desc",
                "Location",
                PlantStatus.HEALTHY
        );

        mockMvc.perform(post("/api/plants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with id: 99999"));
    }

    @Test
    @DisplayName("Validation: Creating plant with blank name returns 400 Bad Request")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testCreatePlant_BlankName_BadRequest() throws Exception {
        PlantRequest request = new PlantRequest(
                "",
                herbCategory.getId(),
                "Species",
                "Desc",
                "Location",
                PlantStatus.HEALTHY
        );

        mockMvc.perform(post("/api/plants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("FR-PLANT-02: User retrieves only their own plants")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetMyPlants_UserOwnership() throws Exception {
        // Plant for Alice
        plantRepository.save(new Plant(userA, herbCategory, "Alice's Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        plantRepository.save(new Plant(userA, succulentCategory, "Alice's Aloe", "Aloe vera", "Desc", "Living Room", PlantStatus.HEALTHY));

        // Plant for Bob
        plantRepository.save(new Plant(userB, herbCategory, "Bob's Mint", "Mentha", "Desc", "Kitchen", PlantStatus.HEALTHY));

        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice's Basil", "Alice's Aloe")))
                .andExpect(jsonPath("$[*].name", not(hasItem("Bob's Mint"))));
    }

    @Test
    @DisplayName("FR-PLANT-05 to 07: Search and filter user plants")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testSearchAndFilterPlants() throws Exception {
        plantRepository.save(new Plant(userA, herbCategory, "Italian Basil", "Ocimum", "Herb", "Balcony", PlantStatus.HEALTHY));
        plantRepository.save(new Plant(userA, herbCategory, "Peppermint", "Mentha", "Herb", "Kitchen", PlantStatus.NEEDS_ATTENTION));
        plantRepository.save(new Plant(userA, succulentCategory, "Jade Plant", "Crassula", "Succulent", "Office", PlantStatus.HEALTHY));

        // Filter by category
        mockMvc.perform(get("/api/plants?category=" + herbCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Italian Basil", "Peppermint")));

        // Filter by status
        mockMvc.perform(get("/api/plants?status=NEEDS_ATTENTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Peppermint"));

        // Search by keyword
        mockMvc.perform(get("/api/plants?search=jade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Jade Plant"));
    }

    @Test
    @DisplayName("FR-PLANT-08: User can view single plant by ID if owner")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetPlantById_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(userA, herbCategory, "Rosemary", "Salvia", "Cooking", "Balcony", PlantStatus.HEALTHY));

        mockMvc.perform(get("/api/plants/" + plant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plant.getId()))
                .andExpect(jsonPath("$.name").value("Rosemary"));
    }

    @Test
    @DisplayName("FR-PLANT-08: User cannot view another user's plant (Returns 404 Not Found)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testGetPlantById_NonOwner_Returns404() throws Exception {
        Plant bobsPlant = plantRepository.save(new Plant(userB, herbCategory, "Bob's Secret Thyme", "Thymus", "Private", "Roof", PlantStatus.HEALTHY));

        mockMvc.perform(get("/api/plants/" + bobsPlant.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Plant not found with id: " + bobsPlant.getId()));
    }

    @Test
    @DisplayName("FR-PLANT-03: User can update own plant")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdatePlant_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(userA, herbCategory, "Old Basil", "Ocimum", "Old", "Room", PlantStatus.HEALTHY));

        PlantRequest updateRequest = new PlantRequest(
                "Fresh Genovese Basil",
                herbCategory.getId(),
                "Ocimum basilicum var. genovese",
                "New description",
                "Sunny Balcony",
                PlantStatus.HEALTHY
        );

        mockMvc.perform(put("/api/plants/" + plant.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fresh Genovese Basil"))
                .andExpect(jsonPath("$.location").value("Sunny Balcony"));
    }

    @Test
    @DisplayName("FR-PLANT-08: User cannot update another user's plant (Returns 404)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdatePlant_NonOwner_Returns404() throws Exception {
        Plant bobsPlant = plantRepository.save(new Plant(userB, herbCategory, "Bob's Plant", "Species", "Desc", "Loc", PlantStatus.HEALTHY));

        PlantRequest updateRequest = new PlantRequest("Hacked Name", herbCategory.getId(), "Sp", "Desc", "Loc", PlantStatus.HEALTHY);

        mockMvc.perform(put("/api/plants/" + bobsPlant.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Endpoint 11: Quick status update via PATCH /api/plants/{id}/status")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testUpdatePlantStatus_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(userA, herbCategory, "Rosemary", "Salvia", "Desc", "Balcony", PlantStatus.HEALTHY));

        PlantStatusRequest statusRequest = new PlantStatusRequest(PlantStatus.NEEDS_ATTENTION);

        mockMvc.perform(patch("/api/plants/" + plant.getId() + "/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plant.getId()))
                .andExpect(jsonPath("$.status").value("NEEDS_ATTENTION"));
    }

    @Test
    @DisplayName("FR-PLANT-04: User can delete own plant")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDeletePlant_Owner_Success() throws Exception {
        Plant plant = plantRepository.save(new Plant(userA, herbCategory, "Temporary Plant", "Sp", "Desc", "Loc", PlantStatus.HEALTHY));

        mockMvc.perform(delete("/api/plants/" + plant.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(plantRepository.existsById(plant.getId()));
    }

    @Test
    @DisplayName("FR-PLANT-08: User cannot delete another user's plant (Returns 404)")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDeletePlant_NonOwner_Returns404() throws Exception {
        Plant bobsPlant = plantRepository.save(new Plant(userB, herbCategory, "Bob's Plant", "Sp", "Desc", "Loc", PlantStatus.HEALTHY));

        mockMvc.perform(delete("/api/plants/" + bobsPlant.getId())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Referenced Category Deletion Protection: Admin cannot delete category referenced by plant (Returns 409 Conflict)")
    @WithMockUser(username = "admin@plantpal.local", roles = {"ADMIN"})
    void testCategoryDeletionProtection_WhenReferencedByPlant() throws Exception {
        // Save plant referencing herbCategory
        plantRepository.save(new Plant(userA, herbCategory, "Attached Plant", "Species", "Desc", "Balcony", PlantStatus.HEALTHY));

        mockMvc.perform(delete("/api/admin/categories/" + herbCategory.getId())
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete: plants are using this category"));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access to /api/plants returns 401")
    void testUnauthenticated_AccessPlants_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/plants/1"))
                .andExpect(status().isUnauthorized());
    }
}