package com.hotelmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementApplication.class, args);
        System.out.println("\n=======================================================");
        System.out.println("  Hotel Management System Backend Started Successfully!");
        System.out.println("  Port: 8080 | Base URL: http://localhost:8080/api");
        System.out.println("=======================================================\n");
    }
}
