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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active = true;

    @ManyToOne
    private User user;
}