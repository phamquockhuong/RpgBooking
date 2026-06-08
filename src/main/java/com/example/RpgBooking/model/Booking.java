package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
    name = "booking",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_booking_room_time_status",
            columnNames = {"start_time", "end_time", "status", "room_id", "booking_date"}
        )
    }
)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    private double totalPrice;
    private double discountPrice;
    private double gst;

    @Column(nullable = false)
    @ColumnDefault("'CREATED'")
    private String status = "CREATED"; // CREATED / PENDING / CONFIRMED / CANCELLED

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_token")
    private String paymentToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
