package com.plantpal.config;

import com.plantpal.entity.User;
import com.plantpal.enums.Role;
import com.plantpal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer executes on application startup.
 * Seeds default admin credentials idempotently and ensures DB connectivity.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("==================================================");
        logger.info(" PlantPal Platform Initializing (Milestone 2)     ");

        // Seed default Admin user idempotently
        String adminEmail = "admin@plantpal.local";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    "System Administrator",
                    adminEmail,
                    passwordEncoder.encode("Admin@123"),
                    Role.ADMIN
            );
            userRepository.save(admin);
            logger.info(" Default Admin Account Created: {}", adminEmail);
        } else {
            logger.info(" Default Admin Account already present: {}", adminEmail);
        }

        logger.info(" Monolith running on port 8080                   ");
        logger.info(" MySQL Connection Active: plantpal_db             ");
        logger.info("==================================================");
    }
}