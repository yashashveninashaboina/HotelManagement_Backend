package com.hotelmanagement.service;

import com.hotelmanagement.dto.PaymentRequest;
import com.hotelmanagement.dto.PaymentResponse;
import com.hotelmanagement.exception.InvalidBookingException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.model.Booking;
import com.hotelmanagement.model.Payment;
import com.hotelmanagement.model.PaymentStatus;
import com.hotelmanagement.repository.BookingRepository;
import com.hotelmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public PaymentResponse processPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        // Check if payment already exists for this booking
        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new InvalidBookingException("Payment has already been processed for Booking ID: " + booking.getId());
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for Booking ID: " + bookingId));
        return mapToPaymentResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        if (payment == null) {
            throw new ResourceNotFoundException("Payment details are unavailable");
        }

        Booking booking = payment.getBooking();
        String customerName = booking != null && booking.getCustomer() != null
                ? booking.getCustomer().getName()
                : "Unknown Customer";
        String roomNumber = booking != null && booking.getRoom() != null
                ? booking.getRoom().getRoomNumber()
                : "N/A";

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(booking != null ? booking.getId() : null)
                .customerName(customerName)
                .roomNumber(roomNumber)
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate() : LocalDateTime.now())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}
