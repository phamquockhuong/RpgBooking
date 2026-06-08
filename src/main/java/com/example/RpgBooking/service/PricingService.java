package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Booking;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateBasePrice(Booking booking) {

        BigDecimal adultPrice = BigDecimal.valueOf(booking.getRoom().getPriceAdult());
        BigDecimal kidPrice = BigDecimal.valueOf(booking.getRoom().getPriceKid());

        return adultPrice.multiply(BigDecimal.valueOf(booking.getNumAdult()))
                .add(kidPrice.multiply(BigDecimal.valueOf(booking.getNumKid())));
    }

    public BigDecimal calculateTax(BigDecimal basePrice) {
        return basePrice.multiply(BigDecimal.valueOf(0.1)); // 10% GST
    }

    public BigDecimal calculateTotal(BigDecimal base, BigDecimal tax, BigDecimal discount) {
        return base.add(tax).subtract(discount);
    }
}
