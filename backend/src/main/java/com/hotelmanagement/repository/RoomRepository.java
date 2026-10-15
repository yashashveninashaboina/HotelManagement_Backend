package com.hotelmanagement.repository;

import com.hotelmanagement.model.Room;
import com.hotelmanagement.model.RoomStatus;
import com.hotelmanagement.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    Boolean existsByRoomNumber(String roomNumber);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
    List<Room> findByRoomTypeAndCapacityGreaterThanEqual(RoomType roomType, Integer capacity);
}
