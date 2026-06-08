package com.example.RpgBooking.service;

import com.example.RpgBooking.dto.PaymentSummary;
import com.example.RpgBooking.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingService bookingService;
    private final PricingService pricingService;
    private final CouponService couponService;

    public PaymentSummary calculate(Long bookingId, String couponCode) {

        Booking booking = bookingService.getById(bookingId);

        BigDecimal base = pricingService.calculateBasePrice(booking);
        BigDecimal tax = pricingService.calculateTax(base);
        BigDecimal discount = couponService.applyCoupon(couponCode, base);

        BigDecimal total = pricingService.calculateTotal(base, tax, discount);

        return new PaymentSummary(base, tax, discount, total);
    }

    public void pay(Long bookingId, String couponCode) {

        PaymentSummary summary = calculate(bookingId, couponCode);

        // giả lập payment success
        bookingService.confirm(bookingId);
    }
}
