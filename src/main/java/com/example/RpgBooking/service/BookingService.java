package com.example.RpgBooking.service;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.BookingStatus;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.BookingRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking createPendingBooking(BookingRequest req,
                                        org.springframework.security.core.userdetails.User principal) {

        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean isBooked = bookingRepository.existsConflict(
                req.getRoomId(),
                req.getBookingDate(),
                LocalTime.parse(req.getStartTime())
        );

        if (isBooked) {
            throw new RuntimeException("Time slot already booked");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setBookingDate(req.getBookingDate());
        booking.setStartTime(LocalTime.parse(req.getStartTime()));
        booking.setNumAdult(req.getNumAdult());
        booking.setNumKid(req.getNumKid());

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            booking.setUser(user);
        } else {
            User user = new User();
            user.setEmail(req.getEmail());
            user.setUsername(req.getEmail());
            user.setPassword("GUEST");
            user.setRole("ROLE_USER");

            userRepository.save(user);

            booking.setUser(user);
        }

        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        return bookingRepository.save(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void confirm(Long bookingId) {
        Booking booking = getById(bookingId);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    public void cancel(Long bookingId) {
        Booking booking = getById(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
