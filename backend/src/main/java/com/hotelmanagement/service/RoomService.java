package com.hotelmanagement.service;

import com.hotelmanagement.dto.RoomRequest;
import com.hotelmanagement.exception.InvalidBookingException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.model.Room;
import com.hotelmanagement.model.RoomStatus;
import com.hotelmanagement.model.RoomType;
import com.hotelmanagement.repository.BookingRepository;
import com.hotelmanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Room createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new InvalidBookingException("Room number already exists: " + request.getRoomNumber());
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .capacity(request.getCapacity())
                .pricePerNight(request.getPricePerNight())
                .status(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE)
                .build();

        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    public Room updateRoom(Long id, RoomRequest request) {
        Room room = getRoomById(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }

    // Filter available rooms with optional roomType, capacity, and date range
    public List<Room> getAvailableRooms(RoomType roomType, Integer capacity, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                // 1. Filter out rooms in MAINTENANCE
                .filter(room -> room.getStatus() != RoomStatus.MAINTENANCE)
                // 2. Filter by roomType if provided
                .filter(room -> roomType == null || room.getRoomType() == roomType)
                // 3. Filter by minimum capacity if provided
                .filter(room -> capacity == null || room.getCapacity() >= capacity)
                // 4. If dates are provided, check if room is free during checkInDate to checkOutDate
                .filter(room -> {
                    if (checkInDate != null && checkOutDate != null) {
                        return !bookingRepository.existsOverlappingBooking(room.getId(), checkInDate, checkOutDate);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
