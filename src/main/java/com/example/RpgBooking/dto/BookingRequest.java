package com.example.RpgBooking.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long roomId;
    private LocalDate bookingDate;
    private String startTime;
    private int numAdult = 1;
    private int numKid = 1;
    private String voucherCode;
    private String email;
}
