package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.RoomBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoomBlockRepository extends JpaRepository<RoomBlock, Long> {

    @Query("""
    SELECT COUNT(rb) > 0
    FROM RoomBlock rb
    WHERE rb.room.id = :roomId
    AND (
        :startDateTime < rb.endTime
        AND :endDateTime > rb.startTime
    )
    """)
    boolean existsConflictBlock(
            @Param("roomId") Long roomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    boolean existsByRoomIdAndStartTimeAndEndTime(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    boolean existsByRoomIdAndStartTimeAndEndTimeAndIdNot(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long id
    );

    @Query("SELECT r FROM RoomBlock r WHERE " +
            "LOWER(r.room.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<RoomBlock> searchBookings(@Param("keyword") String keyword, Pageable pageable);
}
