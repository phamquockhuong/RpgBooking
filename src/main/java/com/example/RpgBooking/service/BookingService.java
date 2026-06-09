package com.example.RpgBooking.service;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.BookingStatus;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.model.User;
import com.example.RpgBooking.repository.BookingRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking createPendingBooking(BookingRequest req,
                                        org.springframework.security.core.userdetails.User principal,
                                        HttpServletRequest request) {

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

            String email = req.getEmail();

            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email is required for guest booking");
            }

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {

                user = new User();
                user.setEmail(email);
                user.setUsername(email);

                String rawPassword = java.util.UUID.randomUUID()
                        .toString()
                        .substring(0, 10);

                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setRole("ROLE_USER");

                userRepository.save(user);

                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Tài khoản RPG Booking");

                    message.setText(
                            "Tài khoản của bạn đã được tạo:\n" +
                                    "Email: " + email + "\n" +
                                    "Password: " + rawPassword
                    );

                    mailSender.send(message);

                } catch (Exception e) {
                    System.out.println("Mail error: " + e.getMessage());
                }
            }

            autoLogin(user, request);

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

    public void autoLogin(User user, HttpServletRequest request) {

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole() != null
                                ? user.getRole().replace("ROLE_", "")
                                : "USER")
                        .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }
}
