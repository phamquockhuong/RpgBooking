package com.example.RpgBooking.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(
    name = "room_block",
    uniqueConstraints = {
        @UniqueConstraint(
                name = "uc_room_start_end",
                columnNames = {"room_id", "start_time", "end_time"}
        )
    }
)
public class RoomBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private String reason;
}
