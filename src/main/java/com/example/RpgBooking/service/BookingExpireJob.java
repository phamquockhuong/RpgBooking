package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.BookingStatus;
import com.example.RpgBooking.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingExpireJob {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookings() {

        List<Booking> bookings =
                bookingRepository.findByStatusAndExpiresAtBefore(
                        BookingStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.EXPIRED);
        }

        bookingRepository.saveAll(bookings);
    }
}
