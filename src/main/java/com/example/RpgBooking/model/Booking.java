package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate bookingDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false)
    private int numAdult;

    @Column(nullable = false)
    private int numKid;

    private double totalPrice;
    private double discountPrice;
    private double gst;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CREATED;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentToken;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Room room;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
