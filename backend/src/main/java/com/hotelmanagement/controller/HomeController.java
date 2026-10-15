package com.hotelmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Hotel Management System Backend API is running successfully!");
        response.put("publicEndpoints", List.of(
                "POST /api/auth/login",
                "POST /api/auth/register",
                "GET /api/rooms",
                "GET /api/rooms/available"
        ));
        return ResponseEntity.ok(response);
    }
}
