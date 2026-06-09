package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
    FROM Booking b
    WHERE b.room.id = :roomId
    AND b.bookingDate = :bookingDate
    AND b.status IN ('PENDING', 'CONFIRMED')
    AND (:startTime < b.endTime AND :endTime > b.startTime)
    """)
    boolean existsConflict(
            @Param("roomId") Long roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
        select b
        from Booking b
        where b.status = 'PENDING'
        and b.expiresAt < :now
    """)
    List<Booking> findExpiredBookings(LocalDateTime now);

    Optional<Booking> findByRoomIdAndBookingDateAndUserIdAndStatus(Long id, LocalDate bookingDate, Long id1, BookingStatus bookingStatus);

    List<Booking> findByRoomIdAndBookingDateAndStatus(Long roomId, LocalDate date, BookingStatus bookingStatus);

    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus bookingStatus, LocalDateTime now);
}