package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Coupon;
import com.example.RpgBooking.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public BigDecimal applyCoupon(String code, BigDecimal basePrice) {

        if (code == null || code.isBlank()) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid Coupon"));

        validateCoupon(coupon);

        if ("PERCENT".equalsIgnoreCase(coupon.getDiscountType())) {
            return basePrice
                    .multiply(BigDecimal.valueOf(coupon.getDiscountValue()))
                    .divide(BigDecimal.valueOf(100));
        }

        return BigDecimal.valueOf(coupon.getDiscountValue());
    }

    private void validateCoupon(Coupon coupon) {

        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon inactive");
        }

        if (now.isBefore(coupon.getStartDate())) {
            throw new RuntimeException("Coupon not started yet");
        }

        if (now.isAfter(coupon.getEndDate())) {
            throw new RuntimeException("Coupon expired");
        }
    }
}