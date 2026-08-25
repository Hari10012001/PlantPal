package com.plantpal.service;

import com.plantpal.dto.request.ChangePasswordRequest;
import com.plantpal.dto.request.UpdateProfileRequest;
import com.plantpal.dto.response.ProfileResponse;
import com.plantpal.entity.User;
import com.plantpal.exception.ResourceNotFoundException;
import com.plantpal.repository.PlantRepository;
import com.plantpal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PlantRepository plantRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long totalPlants = plantRepository.countByUserId(userId);
        return new ProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                totalPlants
        );
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFullName(request.getFullName().trim());
        User updated = userRepository.save(user);

        long totalPlants = plantRepository.countByUserId(userId);
        return new ProfileResponse(
                updated.getId(),
                updated.getFullName(),
                updated.getEmail(),
                updated.getRole(),
                updated.getCreatedAt(),
                totalPlants
        );
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Verify current password matches BCrypt hash
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 2. Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        // 3. Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}