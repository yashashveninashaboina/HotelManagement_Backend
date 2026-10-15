package com.hotelmanagement.dto;

import com.hotelmanagement.model.BookingStatus;
import com.hotelmanagement.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;
    private Double pricePerNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private Double totalAmount;
}
