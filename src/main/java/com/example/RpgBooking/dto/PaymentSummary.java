package com.example.RpgBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentSummary {
    private double price;
    private double gst;
    private double discount;
    private double total;
}
