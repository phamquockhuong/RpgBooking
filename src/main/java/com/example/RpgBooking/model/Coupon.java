package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String discountType;

    private double discountValue;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "validity_start_date", nullable = false)
    private LocalDateTime validityStartDate;

    @Column(name = "validity_end_date", nullable = false)
    private LocalDateTime validityEndDate;

    private boolean active;

    @ManyToOne
    private User user;
}
