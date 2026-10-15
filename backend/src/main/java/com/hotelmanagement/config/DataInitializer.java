package com.hotelmanagement.config;

import com.hotelmanagement.model.*;
import com.hotelmanagement.repository.CustomerRepository;
import com.hotelmanagement.repository.RoomRepository;
import com.hotelmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Admin User
        if (!userRepository.existsByEmail("admin@hotel.com")) {
            User admin = User.builder()
                    .name("Hotel Admin")
                    .email("admin@hotel.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("-> Seeded Admin User: admin@hotel.com / admin123");
        }

        // 2. Seed Receptionist User
        if (!userRepository.existsByEmail("receptionist@hotel.com")) {
            User receptionist = User.builder()
                    .name("Front Desk Receptionist")
                    .email("receptionist@hotel.com")
                    .password(passwordEncoder.encode("receptionist123"))
                    .role(Role.RECEPTIONIST)
                    .build();
            userRepository.save(receptionist);
            System.out.println("-> Seeded Receptionist User: receptionist@hotel.com / receptionist123");
        }

        // 3. Seed Sample Customer User & Customer Profile
        if (!userRepository.existsByEmail("yashashveni@gmail.com")) {
            User customerUser = User.builder()
                    .name("Yashashveni")
                    .email("yashashveni@gmail.com")
                    .password(passwordEncoder.encode("customer123"))
                    .role(Role.CUSTOMER)
                    .build();
            userRepository.save(customerUser);

            Customer customerProfile = Customer.builder()
                    .name("Yashashveni")
                    .email("yashashveni@gmail.com")
                    .phone("9876543210")
                    .address("123 Main Street, City")
                    .build();
            customerRepository.save(customerProfile);
            System.out.println("-> Seeded Sample Customer: yashashveni@gmail.com / customer123");
        }

        // 4. Seed Sample Rooms
        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder()
                    .roomNumber("101")
                    .roomType(RoomType.SINGLE)
                    .capacity(1)
                    .pricePerNight(1500.0)
                    .status(RoomStatus.AVAILABLE)
                    .build());

            roomRepository.save(Room.builder()
                    .roomNumber("102")
                    .roomType(RoomType.DOUBLE)
                    .capacity(2)
                    .pricePerNight(2500.0)
                    .status(RoomStatus.AVAILABLE)
                    .build());

            roomRepository.save(Room.builder()
                    .roomNumber("201")
                    .roomType(RoomType.DELUXE)
                    .capacity(2)
                    .pricePerNight(4000.0)
                    .status(RoomStatus.AVAILABLE)
                    .build());

            roomRepository.save(Room.builder()
                    .roomNumber("301")
                    .roomType(RoomType.SUITE)
                    .capacity(4)
                    .pricePerNight(7500.0)
                    .status(RoomStatus.AVAILABLE)
                    .build());

            System.out.println("-> Seeded Sample Rooms (101, 102, 201, 301)");
        }
    }
}
