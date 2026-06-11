package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findAll(Pageable pageable);
    Page<Room> findByActiveTrue(Pageable pageable);
    List<Room> findByActiveTrue();

    Page<Room> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    List<Room> findFirst6ByActiveTrue();

    @Query("SELECT b.room FROM Booking b WHERE b.bookingDate >= :date GROUP BY b.room ORDER BY COUNT(b.id) DESC")
    List<Room> findTopBookedRooms(@Param("date") java.time.LocalDate date, Pageable pageable);

    long count();
}