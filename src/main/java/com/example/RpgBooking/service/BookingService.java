package com.example.RpgBooking.service;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.dto.PaymentSummary;
import com.example.RpgBooking.exception.BookingConflictException;
import com.example.RpgBooking.model.*;
import com.example.RpgBooking.repository.BookingRepository;
import com.example.RpgBooking.repository.RoomBlockRepository;
import com.example.RpgBooking.repository.RoomRepository;
import com.example.RpgBooking.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomBlockRepository roomBlockRepository;
    private final CouponService couponService;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking createPendingBooking(BookingRequest req,
                                        org.springframework.security.core.userdetails.User principal,
                                        HttpServletRequest request) {

        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trò chơi"));


        LocalTime startTime = LocalTime.parse(req.getStartTime());
        LocalTime endTime = startTime.plusMinutes(room.getDuration());
        int totalPlayers = req.getNumAdult() + req.getNumKid();

        LocalDate bookingDate = req.getBookingDate();
        LocalDate today = LocalDate.now();

        LocalDateTime startDateTime = LocalDateTime.of(req.getBookingDate(), startTime);
        LocalDateTime endDateTime = LocalDateTime.of(req.getBookingDate(), endTime);

        boolean blocked = roomBlockRepository.existsConflictBlock(
                room.getId(),
                startDateTime,
                endDateTime
        );

        if (blocked) {
            throw new RuntimeException("Phòng đã bị khóa trong khoảng thời gian này");
        }

        if (bookingDate.isBefore(today)) {
            throw new RuntimeException("Không thể đặt ngày trong quá khứ");
        }

        if (totalPlayers < room.getMinPlayers() || totalPlayers > room.getMaxPlayers()) {
            throw new com.example.RpgBooking.exception.InvalidPlayerCountException(
                    "Số lượng người chơi không hợp lệ. Phòng này hỗ trợ từ "
                            + room.getMinPlayers() + " đến " + room.getMaxPlayers() + " người."
            );
        }

        if (bookingDate.equals(today)
                && startTime.isBefore(LocalTime.now())) {
            throw new RuntimeException("Không thể đặt giờ trong quá khứ");
        }

        if (req.getNumAdult() < 1) {
            throw new RuntimeException("Phải có ít nhất 1 người lớn");
        }

        if (req.getNumKid() < 1) {
            throw new RuntimeException("Số trẻ em không hợp lệ");
        }

        User user;
        if (principal != null) {
            user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không thấy người dùng này"));
        } else {
            String email = req.getEmail();
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email là bắt buộc cho booking");
            }

            user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(email);

                String rawPassword = UUID.randomUUID().toString().substring(0, 10);
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setRole("ROLE_USER");

                userRepository.save(user);

                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Tài khoản RPG Booking");
                    message.setText(
                            "Cảm ơn bạn đã đặt dịch vụ tại RPG Booking!\n" +
                                    "Tài khoản hệ thống của bạn đã được tạo tự động:\n" +
                                    "Email: " + email + "\n" +
                                    "Mật khẩu: " + rawPassword + "\n\n" +
                                    "Bạn có thể dùng tài khoản này để quản lý các lịch đặt phòng sau này."
                    );
                    mailSender.send(message);
                } catch (Exception e) {
                    System.err.println("Mail error: " + e.getMessage());
                }
            }

            autoLogin(user, request);
        }

        Optional<Booking> existingPendingBooking = bookingRepository
                .findByRoomIdAndBookingDateAndUserIdAndStatus(
                        room.getId(),
                        bookingDate,
                        user.getId(),
                        BookingStatus.PENDING
                );

        if (existingPendingBooking.isPresent()) {
            Booking oldBooking = existingPendingBooking.get();

            oldBooking.setStartTime(startTime);
            oldBooking.setEndTime(endTime);
            oldBooking.setNumAdult(req.getNumAdult());
            oldBooking.setNumKid(req.getNumKid());
            oldBooking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

            return bookingRepository.save(oldBooking);
        }

        boolean isBooked = bookingRepository.existsConflict(
                req.getRoomId(),
                bookingDate,
                startTime,
                endTime
        );

        if (isBooked) {
            throw new BookingConflictException("Trò chơi này đã bị bảo trì");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setNumAdult(req.getNumAdult());
        booking.setNumKid(req.getNumKid());
        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        return bookingRepository.save(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
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

    public List<Booking> getConfirmedBookingsByRoomAndDate(Long roomId, LocalDate date) {
        return bookingRepository.findByRoomIdAndBookingDateAndStatus(roomId, date, BookingStatus.CONFIRMED);
    }

    @Transactional
    public void confirmPayment(Long bookingId) {

        Booking booking = getById(bookingId);

        PaymentSummary payment =
                couponService.calculatePayment(
                        booking,
                        booking.getCoupon()
                );

        booking.setGst(payment.getGst());
        booking.setDiscountPrice(payment.getDiscount());
        booking.setTotalPrice(payment.getTotal());

        booking.setStatus(BookingStatus.CONFIRMED);

        if (booking.getCoupon() != null) {
            couponService.useCoupon(booking.getCoupon());
        }

        bookingRepository.save(booking);

        sendBookingMail(booking);
    }

    private void sendBookingMail(Booking booking) {

        if (booking.getUser() == null
                || booking.getUser().getEmail() == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());
        message.setSubject("Xác nhận đặt phòng RPG Booking");

        String couponInfo = booking.getCoupon() != null
                ? "\nVoucher: " + booking.getCoupon().getCode()
                : "";

        message.setText(
                "Cảm ơn bạn đã đặt phòng tại RPG Booking.\n\n" +
                        "Mã booking: #" + booking.getId() + "\n" +
                        "Phòng: " + booking.getRoom().getName() + "\n" +
                        "Ngày: " + booking.getBookingDate() + "\n" +
                        "Thời gian: " + booking.getStartTime() + " - " + booking.getEndTime() + "\n" +
                        "Người lớn: " + booking.getNumAdult() + "\n" +
                        "Trẻ em: " + booking.getNumKid() +
                        couponInfo +
                        "\n\nTrạng thái: ĐÃ THANH TOÁN"
        );

        mailSender.send(message);
    }
}
