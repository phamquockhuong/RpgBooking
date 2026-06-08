package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
        AND (
            (:start BETWEEN b.startTime AND b.endTime)
            OR (:end BETWEEN b.startTime AND b.endTime)
        )
    """)
    List<Booking> findConflicts(Long roomId,
                                LocalDateTime start,
                                LocalDateTime end);
}
