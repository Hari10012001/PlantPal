package com.plantpal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantpal.dto.request.LoginRequest;
import com.plantpal.dto.request.RegisterRequest;
import com.plantpal.entity.User;
import com.plantpal.enums.Role;
import com.plantpal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_ADMIN_EMAIL = "admin.test@plantpal.local";
    private static final String TEST_ADMIN_PASSWORD = "TestAdminPassword@123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Seed dedicated test admin user for testing
        User testAdmin = new User(
                "Test Administrator",
                TEST_ADMIN_EMAIL,
                passwordEncoder.encode(TEST_ADMIN_PASSWORD),
                Role.ADMIN
        );
        userRepository.save(testAdmin);
    }

    @Test
    @DisplayName("FR-AUTH-01: Successful user registration returns 201 Created and requires login")
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Green Thumb",
                "gardener@plantpal.local",
                "secret123",
                "secret123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful. Please login."));

        assertTrue(userRepository.existsByEmail("gardener@plantpal.local"));
    }

    @Test
    @DisplayName("Validation: Registration with password mismatch returns 400 Bad Request")
    void testRegister_PasswordMismatch() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Green Thumb",
                "mismatch@plantpal.local",
                "secret123",
                "different123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    @DisplayName("Validation: Registration with duplicate email returns 409 Conflict")
    void testRegister_DuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "User One",
                "duplicate@plantpal.local",
                "secret123",
                "secret123"
        );

        // First registration
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    @DisplayName("Validation: Registration with invalid fields returns 400 with field errors")
    void testRegister_ValidationErrors() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "", // Blank name
                "invalid-email", // Bad email
                "123", // Too short password (< 6 chars)
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("FR-AUTH-03: Successful login establishes session and returns UserResponse with no password")
    void testLogin_Success() throws Exception {
        // Register user
        RegisterRequest regRequest = new RegisterRequest(
                "Plant Lover",
                "lover@plantpal.local",
                "Password@123",
                "Password@123"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Login
        LoginRequest loginRequest = new LoginRequest("lover@plantpal.local", "Password@123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("Plant Lover"))
                .andExpect(jsonPath("$.email").value("lover@plantpal.local"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // Verify authenticated GET /api/auth/me using active session
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lover@plantpal.local"))
                .andExpect(jsonPath("$.fullName").value("Plant Lover"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("FR-AUTH-03: Login with invalid password returns 401 Unauthorized")
    void testLogin_InvalidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_ADMIN_EMAIL, "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("FR-AUTH-06: Unauthenticated request to /api/auth/me returns 401 Unauthorized")
    void testGetMe_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Admin Seed & Login: Admin user can login and has ROLE_ADMIN")
    void testAdmin_LoginSuccess() throws Exception {
        LoginRequest adminLogin = new LoginRequest(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_ADMIN_EMAIL))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.fullName").value("Test Administrator"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("FR-AUTH-04: Logout invalidates session and subsequent /me returns 401")
    void testLogout_Success() throws Exception {
        LoginRequest adminLogin = new LoginRequest(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // Logout with CSRF token
        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        // Verify session is now invalidated / unauthorized
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }
}