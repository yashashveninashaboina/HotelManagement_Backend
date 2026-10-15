package com.hotelmanagement.repository;

import com.hotelmanagement.model.Booking;
import com.hotelmanagement.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by customer email
    List<Booking> findByCustomerEmail(String email);

    // Find bookings for a specific room
    List<Booking> findByRoomId(Long roomId);

    // Find bookings by status (e.g. BOOKED, CHECKED_IN, COMPLETED, CANCELLED)
    List<Booking> findByStatus(BookingStatus status);

    // Find bookings belonging to a specific customer ID
    List<Booking> findByCustomerId(Long customerId);

    /**
     * Checks if a room has an existing booking that overlaps with the requested date range.
     * Overlap formula: (requestedCheckIn < existingCheckOut) AND (requestedCheckOut > existingCheckIn)
     * We ignore CANCELLED bookings as they no longer occupy the room.
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status != com.hotelmanagement.model.BookingStatus.CANCELLED " +
           "AND (:checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate)")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkInDate") LocalDate checkInDate,
                                     @Param("checkOutDate") LocalDate checkOutDate);
}
