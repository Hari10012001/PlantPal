package com.plantpal.controller;

import com.plantpal.entity.CareSchedule;
import com.plantpal.entity.Plant;
import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.PlantStatus;
import com.plantpal.enums.Role;
import com.plantpal.enums.SunlightNeeds;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

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

        alice = userRepository.save(new User("Alice Gardner", "alice@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));
        bob = userRepository.save(new User("Bob Planter", "bob@plantpal.local", passwordEncoder.encode("Pass123!"), Role.USER));

        herbCategory = categoryRepository.save(new PlantCategory("Herb", "Cooking herbs"));
        succulentCategory = categoryRepository.save(new PlantCategory("Succulent", "Fleshy desert plants"));
    }

    @Test
    @DisplayName("FR-DASH-01: Empty dashboard returns zero counts and empty arrays for new user")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDashboard_Empty_Success() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlants").value(0))
                .andExpect(jsonPath("$.healthyPlants").value(0))
                .andExpect(jsonPath("$.needsAttentionPlants").value(0))
                .andExpect(jsonPath("$.inactivePlants").value(0))
                .andExpect(jsonPath("$.waterTodayCount").value(0))
                .andExpect(jsonPath("$.overdueCount").value(0))
                .andExpect(jsonPath("$.recentPlants", hasSize(0)))
                .andExpect(jsonPath("$.upcomingCare", hasSize(0)));
    }

    @Test
    @DisplayName("FR-DASH-01 to 03: Dashboard correctly aggregates plant counts and care status")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDashboard_FullStatistics_Success() throws Exception {
        // Plant 1: HEALTHY, Overdue (interval 5, watered 8 days ago -> next was 3 days ago)
        Plant p1 = plantRepository.save(new Plant(alice, herbCategory, "Overdue Basil", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p1, 5, LocalDate.now().minusDays(8), SunlightNeeds.FULL_SUN, null));

        // Plant 2: HEALTHY, Water Today (interval 3, watered 3 days ago -> next is today)
        Plant p2 = plantRepository.save(new Plant(alice, herbCategory, "Due Today Mint", "Mentha", "Desc", "Kitchen", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p2, 3, LocalDate.now().minusDays(3), SunlightNeeds.PARTIAL_SUN, null));

        // Plant 3: NEEDS_ATTENTION, Upcoming in 2 days (interval 7, watered 5 days ago -> next is +2 days)
        Plant p3 = plantRepository.save(new Plant(alice, succulentCategory, "Upcoming Aloe", "Aloe", "Desc", "Room", PlantStatus.NEEDS_ATTENTION));
        careScheduleRepository.save(new CareSchedule(p3, 7, LocalDate.now().minusDays(5), SunlightNeeds.FULL_SUN, null));

        // Plant 4: INACTIVE, Not Set (never watered)
        Plant p4 = plantRepository.save(new Plant(alice, succulentCategory, "Inactive Jade", "Crassula", "Desc", "Office", PlantStatus.INACTIVE));
        careScheduleRepository.save(new CareSchedule(p4, 14, null, SunlightNeeds.SHADE, null));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlants").value(4))
                .andExpect(jsonPath("$.healthyPlants").value(2))
                .andExpect(jsonPath("$.needsAttentionPlants").value(1))
                .andExpect(jsonPath("$.inactivePlants").value(1))
                .andExpect(jsonPath("$.overdueCount").value(1))
                .andExpect(jsonPath("$.waterTodayCount").value(1))
                .andExpect(jsonPath("$.recentPlants", hasSize(4)))
                .andExpect(jsonPath("$.upcomingCare", hasSize(3))); // Overdue, Today, +2 days (excludes Not Set)
    }

    @Test
    @DisplayName("FR-DASH-04: Recent plants caps at 5 and is ordered newest first")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDashboard_RecentPlants_Max5_NewestFirst() throws Exception {
        for (int i = 1; i <= 7; i++) {
            Plant p = plantRepository.save(new Plant(alice, herbCategory, "Plant " + i, "Species", "Desc", "Loc", PlantStatus.HEALTHY));
            careScheduleRepository.save(new CareSchedule(p, 5, null, null, null));
            Thread.sleep(10); // Guarantee distinct created_at
        }

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlants").value(7))
                .andExpect(jsonPath("$.recentPlants", hasSize(5)))
                .andExpect(jsonPath("$.recentPlants[0].name").value("Plant 7"))
                .andExpect(jsonPath("$.recentPlants[1].name").value("Plant 6"))
                .andExpect(jsonPath("$.recentPlants[2].name").value("Plant 5"))
                .andExpect(jsonPath("$.recentPlants[3].name").value("Plant 4"))
                .andExpect(jsonPath("$.recentPlants[4].name").value("Plant 3"));
    }

    @Test
    @DisplayName("FR-DASH-05: Upcoming care only includes dates within next 7 days in chronological order")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDashboard_UpcomingCare_Within7Days_ExcludesFarFutureAndNotSet() throws Exception {
        // Overdue (due -2 days)
        Plant p1 = plantRepository.save(new Plant(alice, herbCategory, "Plant Overdue", "Ocimum", "Desc", "Balcony", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p1, 5, LocalDate.now().minusDays(7), SunlightNeeds.FULL_SUN, null));

        // Due today (due 0 days)
        Plant p2 = plantRepository.save(new Plant(alice, herbCategory, "Plant Today", "Mentha", "Desc", "Kitchen", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p2, 4, LocalDate.now().minusDays(4), SunlightNeeds.FULL_SUN, null));

        // Due in 5 days (within 7 days)
        Plant p3 = plantRepository.save(new Plant(alice, herbCategory, "Plant In 5 Days", "Salvia", "Desc", "Window", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p3, 10, LocalDate.now().minusDays(5), SunlightNeeds.FULL_SUN, null));

        // Due in 12 days (exceeds 7 days -> excluded from upcomingCare)
        Plant p4 = plantRepository.save(new Plant(alice, herbCategory, "Plant In 12 Days", "Thymus", "Desc", "Window", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p4, 15, LocalDate.now().minusDays(3), SunlightNeeds.FULL_SUN, null));

        // Not Set (excluded from upcomingCare)
        Plant p5 = plantRepository.save(new Plant(alice, herbCategory, "Plant Not Set", "Rosmarinus", "Desc", "Window", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(p5, 7, null, SunlightNeeds.FULL_SUN, null));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upcomingCare", hasSize(3)))
                .andExpect(jsonPath("$.upcomingCare[0].plantName").value("Plant Overdue"))
                .andExpect(jsonPath("$.upcomingCare[0].wateringStatus").value("WATER_OVERDUE"))
                .andExpect(jsonPath("$.upcomingCare[1].plantName").value("Plant Today"))
                .andExpect(jsonPath("$.upcomingCare[1].wateringStatus").value("WATER_TODAY"))
                .andExpect(jsonPath("$.upcomingCare[2].plantName").value("Plant In 5 Days"))
                .andExpect(jsonPath("$.upcomingCare[2].wateringStatus").value("WATER_UPCOMING"));
    }

    @Test
    @DisplayName("User Isolation: User A only sees their own statistics and plants, 0 of User B")
    @WithMockUser(username = "alice@plantpal.local", roles = {"USER"})
    void testDashboard_UserIsolation() throws Exception {
        // Alice has 1 plant
        Plant pAlice = plantRepository.save(new Plant(alice, herbCategory, "Alice Plant", "Sp", "Desc", "Loc", PlantStatus.HEALTHY));
        careScheduleRepository.save(new CareSchedule(pAlice, 5, LocalDate.now().minusDays(5), null, null));

        // Bob has 3 plants
        for (int i = 1; i <= 3; i++) {
            Plant pBob = plantRepository.save(new Plant(bob, succulentCategory, "Bob Plant " + i, "Sp", "Desc", "Loc", PlantStatus.HEALTHY));
            careScheduleRepository.save(new CareSchedule(pBob, 2, LocalDate.now().minusDays(5), null, null));
        }

        // Alice views dashboard
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlants").value(1))
                .andExpect(jsonPath("$.healthyPlants").value(1))
                .andExpect(jsonPath("$.recentPlants", hasSize(1)))
                .andExpect(jsonPath("$.recentPlants[0].name").value("Alice Plant"))
                .andExpect(jsonPath("$.upcomingCare", hasSize(1)))
                .andExpect(jsonPath("$.upcomingCare[0].plantName").value("Alice Plant"));
    }

    @Test
    @DisplayName("Authorization: Unauthenticated access returns 401 Unauthorized")
    void testDashboard_Unauthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}