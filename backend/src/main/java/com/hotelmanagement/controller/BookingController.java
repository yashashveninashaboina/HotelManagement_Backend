package com.hotelmanagement.controller;

import com.hotelmanagement.dto.BookingRequest;
import com.hotelmanagement.dto.BookingResponse;
import com.hotelmanagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingResponse booking = bookingService.createBooking(request);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomerEmail(@PathVariable String email) {
        List<BookingResponse> bookings = bookingService.getBookingsByCustomerEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        BookingResponse cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long id) {
        BookingResponse checkInBooking = bookingService.checkIn(id);
        return ResponseEntity.ok(checkInBooking);
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<BookingResponse> checkOut(@PathVariable Long id) {
        BookingResponse checkOutBooking = bookingService.checkOut(id);
        return ResponseEntity.ok(checkOutBooking);
    }
}
