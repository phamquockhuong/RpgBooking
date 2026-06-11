package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.RoomBlock;
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
}
