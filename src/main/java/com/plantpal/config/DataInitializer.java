package com.plantpal.config;

import com.plantpal.entity.PlantCategory;
import com.plantpal.entity.User;
import com.plantpal.enums.Role;
import com.plantpal.repository.PlantCategoryRepository;
import com.plantpal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DataInitializer executes on application startup.
 * Seeds default admin credentials and the 8 approved plant categories idempotently.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PlantCategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@plantpal.local}")
    private String adminEmail;

    @Value("${app.admin.password:${ADMIN_PASSWORD:}}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository,
                           PlantCategoryRepository categoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("==================================================");
        logger.info(" PlantPal Platform Initializing (Milestone 3)     ");

        // 1. Seed default Admin user idempotently if not already present
        if (!userRepository.existsByEmail(adminEmail)) {
            if (adminPassword != null && !adminPassword.isBlank()) {
                User admin = new User(
                        "System Administrator",
                        adminEmail,
                        passwordEncoder.encode(adminPassword.trim()),
                        Role.ADMIN
                );
                userRepository.save(admin);
                logger.info(" Default Admin Account Created: {}", adminEmail);
            } else {
                logger.warn(" ADMIN_PASSWORD environment variable is not set. Default Admin account creation skipped.");
            }
        } else {
            logger.info(" Default Admin Account already present: {}", adminEmail);
        }

        // 2. Seed approved 8 plant categories idempotently
        seedCategories();

        logger.info(" Monolith running on port 8080                   ");
        logger.info(" MySQL Connection Active: plantpal_db             ");
        logger.info("==================================================");
    }

    private void seedCategories() {
        Map<String, String> approvedCategories = new LinkedHashMap<>();
        approvedCategories.put("Herb", "Culinary and aromatic kitchen herbs (e.g., Basil, Mint, Rosemary)");
        approvedCategories.put("Succulent", "Fleshy water-retaining plants (e.g., Aloe Vera, Jade, Echeveria)");
        approvedCategories.put("Flowering", "Ornamental flowering indoor and garden plants (e.g., Jasmine, Peace Lily)");
        approvedCategories.put("Vegetable", "Edible garden plants and crops (e.g., Tomato, Pepper, Spinach)");
        approvedCategories.put("Tree", "Small ornamental, bonsai, or fruit trees (e.g., Ficus, Citrus, Olive)");
        approvedCategories.put("Shrub", "Woody perennial bush varieties (e.g., Hibiscus, Boxwood)");
        approvedCategories.put("Fern", "Non-flowering shade-loving leafy plants (e.g., Boston Fern, Maidenhair)");
        approvedCategories.put("Cactus", "Drought-tolerant spiny desert plants (e.g., Saguaro, Prickly Pear)");

        int createdCount = 0;
        for (Map.Entry<String, String> entry : approvedCategories.entrySet()) {
            String name = entry.getKey();
            String description = entry.getValue();

            if (!categoryRepository.existsByNameIgnoreCase(name)) {
                PlantCategory category = new PlantCategory(name, description);
                categoryRepository.save(category);
                createdCount++;
            }
        }

        if (createdCount > 0) {
            logger.info(" Seeded {} plant categories successfully.", createdCount);
        } else {
            logger.info(" All 8 plant categories already present in database.");
        }
    }
}