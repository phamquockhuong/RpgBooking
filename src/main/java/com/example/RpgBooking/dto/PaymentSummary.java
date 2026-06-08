package com.example.RpgBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentSummary {
    private BigDecimal basePrice;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
}
