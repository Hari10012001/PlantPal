package com.plantpal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("==================================================");
        logger.info(" PlantPal Platform Initialized Successfully (M1) ");
        logger.info(" Monolith running on port 8080                   ");
        logger.info(" MySQL Connection Active: plantpal_db             ");
        logger.info("==================================================");
    }
}