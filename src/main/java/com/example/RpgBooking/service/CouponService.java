package com.example.RpgBooking.service;

import com.example.RpgBooking.dto.PaymentSummary;
import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.Coupon;
import com.example.RpgBooking.model.Event;
import com.example.RpgBooking.repository.BookingRepository;
import com.example.RpgBooking.repository.CouponRepository;
import com.example.RpgBooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public Map<String, Object> applyCoupon(
            Booking booking,
            String code) {

        Map<String, Object> result = new HashMap<>();

        Coupon coupon = couponRepository
                .findByCodeAndActiveTrue(code)
                .orElse(null);

        if (coupon == null) {

            result.put("success", false);
            result.put("message", "Voucher không tồn tại");

            return result;
        }

        if (coupon.getStartDate().isAfter(LocalDateTime.now())
                || coupon.getEndDate().isBefore(LocalDateTime.now())) {

            result.put("success", false);
            result.put("message", "Voucher hết hạn");

            return result;
        }

        PaymentSummary payment = calculatePayment(booking, coupon);

        booking.setCoupon(coupon);

        bookingRepository.save(booking);

        result.put("success", true);
        result.put("price", payment.getPrice());
        result.put("tax", payment.getGst());
        result.put("discount", payment.getDiscount());
        result.put("total", payment.getTotal());

        return result;
    }

    public void useCoupon(Coupon coupon) {

        if (coupon.getQuantity() <= 0) {
            throw new RuntimeException("Voucher đã hết lượt");
        }

        coupon.setQuantity(coupon.getQuantity() - 1);

        if (coupon.getQuantity() == 0) {
            coupon.setActive(false);
        }

        couponRepository.save(coupon);
    }

    public PaymentSummary calculatePayment(
            Booking booking,
            Coupon coupon) {

        double price =
                booking.getNumAdult() * booking.getRoom().getPriceAdult()
                        + booking.getNumKid() * booking.getRoom().getPriceKid();

        Event event = eventRepository.findFirstByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        booking.getBookingDate(),
                        booking.getBookingDate()
                )
                .orElse(null);

        if (event != null) {

            price = price * (event.getPriceMultiplier() / 100.0);
        }

        double gst = price * 0.1;

        double discount = 0;

        if (coupon != null) {

            if ("PERCENT".equals(coupon.getDiscountType())) {

                discount =
                        (price + gst)
                                * coupon.getDiscountValue() / 100;

            } else if ("FIXED".equals(coupon.getDiscountType())) {

                discount = coupon.getDiscountValue();
            }
        }

        double total =
                Math.max(0, price + gst - discount);

        return new PaymentSummary(
                price,
                gst,
                discount,
                total
        );
    }
}