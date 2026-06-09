package com.example.RpgBooking.controller;

import com.example.RpgBooking.dto.BookingRequest;
import com.example.RpgBooking.dto.PaymentSummary;
import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final RoomService roomService;
    private final CategoryService categoryService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @GetMapping("/")
    public String bookingsPage(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 6) Pageable pageable,
            Model model) {

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("topRooms", roomService.getTopBookedRooms());

        Page<Room> roomPage;
        if (categoryId != null) {
            roomPage = roomService.getRoomsByCategoryId(categoryId, pageable);
        } else {
            roomPage = roomService.getActiveRooms(pageable);
        }

        model.addAttribute("roomPage", roomPage);
        model.addAttribute("selectedCategoryId", categoryId);
        return "user/bookings";
    }

    @GetMapping("/booking/{id}")
    public String showBookingPage(@PathVariable Long id, Model model) {
        try {
            Room room = roomService.getRoomById(id);
            model.addAttribute("room", room);
            model.addAttribute("bookingRequest", new BookingRequest());
            return "user/booking-detail";
        } catch (RuntimeException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/booking/create")
    public String create(BookingRequest req,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,HttpServletRequest request) {

        Booking booking = bookingService.createPendingBooking(req, principal, request);

        return "redirect:/booking/" + booking.getId() + "/payment";
    }

    @GetMapping("/booking/{id}/payment")
    public String payment(@PathVariable Long id,
                          @RequestParam(required = false) String voucher,
                          Model model) {

        PaymentSummary summary = paymentService.calculate(id, voucher);

        model.addAttribute("summary", summary);
        model.addAttribute("booking", bookingService.getById(id));

        return "user/payment";
    }

    @PostMapping("/booking/confirm")
    public String confirm(@RequestParam Long bookingId,
                          @RequestParam(required = false) String voucherCode) {

        paymentService.pay(bookingId, voucherCode);

        return "redirect:/booking/success";
    }

    @GetMapping("/about-us")
    public String aboutUsPage() { return "user/about-us"; }

    @GetMapping("/faq")
    public String faqPage() { return "user/faq"; }

    @GetMapping("/contact")
    public String contactPage() { return "user/contact"; }

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}