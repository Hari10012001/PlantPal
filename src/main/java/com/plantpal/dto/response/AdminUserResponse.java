package com.plantpal.dto.response;

import com.plantpal.enums.Role;

import java.time.LocalDateTime;

public class AdminUserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private long plantCount;
    private LocalDateTime createdAt;

    public AdminUserResponse() {
    }

    public AdminUserResponse(Long id, String fullName, String email, Role role, long plantCount, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.plantCount = plantCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getPlantCount() {
        return plantCount;
    }

    public void setPlantCount(long plantCount) {
        this.plantCount = plantCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}