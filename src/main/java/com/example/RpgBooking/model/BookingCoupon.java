package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BookingCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Coupon coupon;

    private double discountAmount;
}
