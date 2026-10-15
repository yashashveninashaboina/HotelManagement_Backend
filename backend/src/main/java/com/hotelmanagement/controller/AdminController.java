package com.hotelmanagement.controller;

import com.hotelmanagement.dto.HotelStatsResponse;
import com.hotelmanagement.model.BookingStatus;
import com.hotelmanagement.model.Payment;
import com.hotelmanagement.model.RoomStatus;
import com.hotelmanagement.repository.BookingRepository;
import com.hotelmanagement.repository.CustomerRepository;
import com.hotelmanagement.repository.PaymentRepository;
import com.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/stats")
    public ResponseEntity<HotelStatsResponse> getHotelStatistics() {
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.findByStatus(RoomStatus.AVAILABLE).size();
        long bookedRooms = roomRepository.findByStatus(RoomStatus.BOOKED).size() + roomRepository.findByStatus(RoomStatus.OCCUPIED).size();
        long totalCustomers = customerRepository.count();
        long totalBookings = bookingRepository.count();

        double totalRevenue = paymentRepository.findAll().stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        HotelStatsResponse stats = HotelStatsResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .bookedRooms(bookedRooms)
                .totalCustomers(totalCustomers)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue)
                .build();

        return ResponseEntity.ok(stats);
    }
}
