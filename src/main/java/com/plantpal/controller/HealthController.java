package com.plantpal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/health", "/health"})
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "PlantPal");
        response.put("version", "1.0.0");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("description", "A Personal Plant Care, Watering Schedule and Growth Monitoring Platform");
        return ResponseEntity.ok(response);
    }
}