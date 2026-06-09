package com.example.RpgBooking.controller.admin;

import com.example.RpgBooking.model.Booking;
import com.example.RpgBooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public String list(Model model, @PageableDefault(size = 15) Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findAll(pageable);

        model.addAttribute("bookings", bookings);
        return "admin/bookings/index";
    }
}
