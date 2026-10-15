package com.hotelmanagement.service;

import com.hotelmanagement.dto.BookingRequest;
import com.hotelmanagement.dto.BookingResponse;
import com.hotelmanagement.exception.InvalidBookingException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.exception.RoomNotAvailableException;
import com.hotelmanagement.model.*;
import com.hotelmanagement.repository.BookingRepository;
import com.hotelmanagement.repository.CustomerRepository;
import com.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    public BookingResponse createBooking(BookingRequest request) {
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();
        LocalDate today = LocalDate.now();

        // 1. Business Rule: Check-in date cannot be in the past
        if (checkIn.isBefore(today)) {
            throw new InvalidBookingException("Check-in date cannot be in the past: " + checkIn);
        }

        // 2. Business Rule: Check-out date must be strictly after check-in date
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingException("Check-out date (" + checkOut + ") must be after check-in date (" + checkIn + ")");
        }

        // 3. Fetch Customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        // 4. Fetch Room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        // 5. Business Rule: Check if room is under maintenance
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is currently under maintenance");
        }

        // 6. Business Rule: Prevent Overlapping Bookings (Double Booking Check)
        boolean isOverlapping = bookingRepository.existsOverlappingBooking(room.getId(), checkIn, checkOut);
        if (isOverlapping) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + 
                    " is already booked for the requested date range (" + checkIn + " to " + checkOut + ")");
        }

        // 7. Billing Calculation: totalAmount = pricePerNight * numberOfNights
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = numberOfNights * room.getPricePerNight();

        // 8. Save Booking
        Booking booking = Booking.builder()
                .customer(customer)
                .room(room)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.BOOKED)
                .totalAmount(totalAmount)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 9. Update Room Status to BOOKED
        room.setStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        return mapToBookingResponse(savedBooking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToBookingResponse(booking);
    }

    public List<BookingResponse> getBookingsByCustomerEmail(String email) {
        return bookingRepository.findByCustomerEmail(email).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingException("Completed bookings cannot be cancelled");
        }

        // Change status to CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Restore room status to AVAILABLE
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return mapToBookingResponse(updatedBooking);
    }

    public BookingResponse checkIn(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new InvalidBookingException("Only bookings with status BOOKED can be checked in. Current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        Booking updatedBooking = bookingRepository.save(booking);

        // Update room status to OCCUPIED
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return mapToBookingResponse(updatedBooking);
    }

    public BookingResponse checkOut(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new InvalidBookingException("Only CHECKED_IN bookings can complete checkout. Current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Restore room status to AVAILABLE
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return mapToBookingResponse(updatedBooking);
    }

    // Helper method to convert Booking Entity to BookingResponse DTO
    public BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getName())
                .customerEmail(booking.getCustomer().getEmail())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getRoomType())
                .pricePerNight(booking.getRoom().getPricePerNight())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .build();
    }
}
