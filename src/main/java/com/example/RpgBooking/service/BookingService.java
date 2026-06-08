package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {

        boolean conflict = !bookingRepository
                .findConflicts(
                        booking.getRoom().getId(),
                        booking.getStartTime(),
                        booking.getEndTime()
                ).isEmpty();

        if (conflict) {
            throw new RuntimeException("Room is already booked in this time slot!");
        }

        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }
}
