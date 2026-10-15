package com.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelStatsResponse {
    private long totalRooms;
    private long availableRooms;
    private long bookedRooms;
    private long totalCustomers;
    private long totalBookings;
    private double totalRevenue;
}
