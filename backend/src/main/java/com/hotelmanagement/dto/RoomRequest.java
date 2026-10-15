package com.hotelmanagement.dto;

import com.hotelmanagement.model.RoomStatus;
import com.hotelmanagement.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private String roomNumber;
    private RoomType roomType;
    private Integer capacity;
    private Double pricePerNight;
    private RoomStatus status;
}
